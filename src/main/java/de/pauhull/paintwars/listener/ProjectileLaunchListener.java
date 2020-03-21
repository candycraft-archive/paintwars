package de.pauhull.paintwars.listener;

import de.pauhull.paintwars.PaintWars;
import de.pauhull.paintwars.game.Team;
import de.pauhull.paintwars.manager.ItemManager;
import de.pauhull.paintwars.util.Trail;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.material.MaterialData;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Paul
 * on 08.12.2018
 *
 * @author pauhull
 */
public class ProjectileLaunchListener extends ListenerTemplate {

    @Getter
    private static List<Player> giveBomb = new ArrayList<>();

    public ProjectileLaunchListener(PaintWars paintWars) {
        super(paintWars);
    }

    @EventHandler
    public void onProjectileLaunch(ProjectileLaunchEvent event) {
        if (event.getEntity() instanceof Snowball && event.getEntity().getShooter() instanceof Player) {
            Snowball snowball = (Snowball) event.getEntity();
            Player player = (Player) snowball.getShooter();
            snowball.setVelocity(snowball.getVelocity().multiply(0.75));

            final Team team = Team.getTeam(player);
            if (team != null) {
                new Trail(paintWars, snowball, () -> {
                    snowball.getLocation().getWorld().spawnParticle(Particle.BLOCK_CRACK, snowball.getLocation(),
                            5, 0.25f, 0.25f, 0.25f,
                            new MaterialData(Material.WOOL, team.getDyeColor().getWoolData()));
                });
            }


            giveBomb.add(player);

            Bukkit.getScheduler().scheduleSyncDelayedTask(paintWars, () -> {
                if (giveBomb.contains(player)) {
                    if (!player.getInventory().contains(Material.SNOW_BALL)) {
                        player.getInventory().addItem(ItemManager.BOMBS);
                    }
                }
            }, 100);
        }
    }

}
