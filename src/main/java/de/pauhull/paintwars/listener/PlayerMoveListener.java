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
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.util.Vector;

import java.util.*;

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
    private Map<Player, Integer> jumpAndRunCheckpoints = new HashMap<>();

    @Getter
    private List<Player> jumpAndRunFinished = new ArrayList<>();

    @Getter
    private Map<Player, LinkedList<Block>> lobbyBlocks = new HashMap<>();

    public PlayerMoveListener(PaintWars paintWars) {
        super(paintWars);
        instance = this;
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {

        Player player = event.getPlayer();
        Block block = player.getLocation().getBlock();

        if (paintWars.getPhaseHandler().getActivePhaseType() == GamePhase.Type.LOBBY) {

            if (block.getType() == Material.IRON_PLATE) {
                if (lobbyBlocks.containsKey(player)) {
                    for (Block replace : lobbyBlocks.get(player)) {
                        replace.setType(Material.BARRIER);
                        replace.setData((byte) 0);
                    }
                    lobbyBlocks.get(player).clear();
                }

                if (!jumpAndRunCheckpoints.containsKey(player)) {
                    player.playSound(player.getLocation(), Sound.NOTE_PLING, 1, 1);
                    player.sendMessage(Messages.PREFIX + "Du hast das §eJump and Run §7begonnen!");
                    paintWars.getItemManager().giveJumpAndRunItems(player);
                    jumpAndRunCheckpoints.put(player, 0);
                    jumpAndRunFinished.remove(player);
                }
            }

            if (block.getType() == Material.GOLD_PLATE && !jumpAndRunFinished.contains(player)) {
                player.playSound(player.getLocation(), Sound.LEVEL_UP, 1, 1);
                player.sendMessage(Messages.PREFIX + "Du hast das Jump and Run §ageschafft§7!");
                RandomFireworkGenerator.shootRandomFirework(player.getLocation(), 5);
                paintWars.getItemManager().giveLobbyItems(player);
                jumpAndRunFinished.add(player);
                jumpAndRunCheckpoints.remove(player);
            }

            if (block.getType() == Material.WOOD_PLATE && jumpAndRunCheckpoints.containsKey(player)) {
                Block signBlock = block.getLocation().subtract(0, 2, 0).getBlock();
                if (signBlock.getType() == Material.SIGN_POST || signBlock.getType() == Material.WALL_SIGN) {
                    Sign sign = (Sign) signBlock.getState();
                    int checkpointId = Integer.parseInt(sign.getLine(0).replace("[CP-", "").replace("]", ""));
                    int currentCheckpointId = jumpAndRunCheckpoints.get(player);
                    if (checkpointId != currentCheckpointId) {
                        jumpAndRunCheckpoints.put(player, checkpointId);
                        player.playSound(player.getLocation(), Sound.NOTE_PLING, 1, 1);
                        player.sendMessage(Messages.PREFIX + "Du hast §aCheckpoint " + checkpointId + "§7 erreicht!");
                    }
                }
            }

            Block barrierBlock = null;
            Block blockUnder = player.getLocation().subtract(0, 1, 0).getBlock();
            Block blockUnderBlockUnder = player.getLocation().subtract(0, 1.3, 0).getBlock();
            if (blockUnder.getType() == Material.BARRIER) {
                barrierBlock = blockUnder;
            } else if (blockUnderBlockUnder.getType() == Material.BARRIER) {
                barrierBlock = blockUnderBlockUnder;
            }

            if (barrierBlock != null && !jumpAndRunCheckpoints.containsKey(player)) {
                LinkedList<Block> blocks;
                if (!lobbyBlocks.containsKey(player)) {
                    blocks = new LinkedList<>();
                    lobbyBlocks.put(player, blocks);
                } else {
                    blocks = lobbyBlocks.get(player);
                }

                if (blocks.size() >= 3) {
                    blocks.getFirst().setType(Material.BARRIER);
                    blocks.getFirst().setData((byte) 0);
                    blocks.removeFirst();
                }

                barrierBlock.setType(Material.WOOL);
                Team team = Team.getTeam(player);
                if (team != null) {
                    barrierBlock.setData(team.getDyeColor().getWoolData());
                }
                blocks.add(barrierBlock);
                final Block finalBarrierBlock = barrierBlock;
                Bukkit.getScheduler().scheduleSyncDelayedTask(paintWars, () -> {
                    if (finalBarrierBlock.getType() == Material.WOOL && player.getLocation().distanceSquared(finalBarrierBlock.getLocation().add(0.5, 1, 0.5)) > 0.75 * 0.75) {
                        finalBarrierBlock.setType(Material.BARRIER);
                        finalBarrierBlock.setData((byte) 0);
                    }
                }, 15);

                lobbyBlocks.put(player, blocks);
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

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        jumpAndRunCheckpoints.remove(player);
        jumpAndRunFinished.remove(player);
    }

    public BlockFace yawToFace(float yaw) {
        return new BlockFace[]{BlockFace.SOUTH, BlockFace.WEST, BlockFace.NORTH, BlockFace.EAST}[(Math.round(yaw / 90.0F) & 0x3)];
    }

}
