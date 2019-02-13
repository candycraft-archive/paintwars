package de.pauhull.paintwars.listener;

import de.pauhull.paintwars.PaintWars;
import de.pauhull.paintwars.game.Team;
import de.pauhull.paintwars.util.MathUtils;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.util.Vector;

import java.util.Random;

/**
 * Created by Paul
 * on 08.12.2018
 *
 * @author pauhull
 */
public class ProjectileHitListener extends ListenerTemplate {

    private static final double RADIUS = 5.0;
    private static final double MAX_DAMAGE = 15.0;
    private static final double MIN_DAMAGE = 5.0;

    private static Random random = new Random();

    public ProjectileHitListener(PaintWars paintWars) {
        super(paintWars);
    }

    @EventHandler
    public void onProjectileHit(ProjectileHitEvent event) {
        if (event.getEntity() instanceof Snowball && event.getEntity().getShooter() instanceof Player) {
            Snowball snowball = (Snowball) event.getEntity();
            Player player = (Player) snowball.getShooter();
            Team team = Team.getTeam(player);

            if (team != null) {

                Vector snowballVelocity = snowball.getVelocity();
                snowballVelocity.normalize();
                snowballVelocity.multiply(-1);

                for (int i = 0; i < 10; i++) {
                    FallingBlock fallingBlock = snowball.getWorld().spawnFallingBlock(snowball.getLocation(),
                            Material.WOOL, team.getDyeColor().getWoolData());
                    fallingBlock.setDropItem(false);
                    Vector velocity = MathUtils.randomizeVector(snowballVelocity, 5, 5, 5);
                    velocity.multiply(0.4);
                    fallingBlock.setVelocity(velocity);
                }

                for (Player damaged : snowball.getWorld().getEntitiesByClass(Player.class)) {
                    Team damagedTeam = Team.getTeam(damaged);

                    if (damagedTeam == null)
                        continue;

                    if (damagedTeam == team && !damaged.getName().equals(player.getName()))
                        continue;

                    double distance = snowball.getLocation().distance(damaged.getLocation());
                    if (distance <= RADIUS) {

                        double damage = MathUtils.map(distance, 0, RADIUS, MAX_DAMAGE, MIN_DAMAGE);

                        EntityDamageEvent entityDamageEvent;

                        if (player.getName().equals(damaged.getName())) {
                            damage *= 0.75;
                            entityDamageEvent = new EntityDamageEvent(damaged, EntityDamageEvent.DamageCause.CUSTOM, damage);
                        } else {
                            entityDamageEvent = new EntityDamageByEntityEvent(player, damaged, EntityDamageEvent.DamageCause.CUSTOM, damage);
                        }

                        Bukkit.getPluginManager().callEvent(entityDamageEvent);
                        if (entityDamageEvent.isCancelled())
                            continue;
                        damage = entityDamageEvent.getDamage();

                        if (damaged.getHealth() - damage < 1)
                            continue;

                        if (!damaged.equals(player)) {
                            damaged.damage(damage, player);
                        } else {
                            damaged.damage(damage);
                        }
                    }
                }

                for (double x = -RADIUS; x <= RADIUS; x += 0.5) {
                    for (double y = -RADIUS; y <= RADIUS; y += 0.5) {
                        for (double z = -RADIUS; z <= RADIUS; z += 0.5) {
                            Location location = snowball.getLocation().clone().add(x, y, z);

                            if (location.distance(snowball.getLocation()) <= RADIUS) {
                                Block block = location.getBlock();
                                if (block.getType() == Material.WOOL && block.getData() != team.getDyeColor().getWoolData()) {
                                    block.setData(team.getDyeColor().getWoolData());

                                    if (paintWars.getColoredBlocks().containsKey(player.getUniqueId())) {
                                        paintWars.getColoredBlocks().get(player.getUniqueId()).incrementAndGet();
                                    }
                                }
                            }
                        }
                    }
                }

                snowball.getWorld().playEffect(snowball.getLocation(), Effect.EXPLOSION_HUGE, 0);
                snowball.getWorld().playSound(snowball.getLocation(), Sound.EXPLODE, 1, 1);
            }
        }
    }

}
