package de.pauhull.paintwars.listener;

import de.pauhull.paintwars.Messages;
import de.pauhull.paintwars.PaintWars;
import de.pauhull.paintwars.manager.ItemManager;
import de.pauhull.paintwars.phase.GamePhase;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

/**
 * Created by Paul
 * on 07.12.2018
 *
 * @author pauhull
 */
public class PlayerInteractListener extends ListenerTemplate {

    public PlayerInteractListener(PaintWars paintWars) {
        super(paintWars);
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        if (paintWars.getPhaseHandler().getActivePhaseType() != GamePhase.Type.INGAME) {
            event.setCancelled(true);
        }

        if (paintWars.getSpectators().contains(player)) {
            event.setCancelled(true);

            if (event.getItem() != null && event.getItem().equals(ItemManager.SPECTATOR)) {
                paintWars.getSpectatorInventory().show(player);
            }
            return;
        }

        if (event.getAction() != Action.RIGHT_CLICK_BLOCK && event.getAction() != Action.RIGHT_CLICK_AIR) {
            return;
        }
        ItemStack stack = event.getItem();

        if (paintWars.getPhaseHandler().getActivePhaseType() == GamePhase.Type.LOBBY && event.getItem() != null) {
            if (stack.equals(ItemManager.LEAVE)) {
                player.kickPlayer("");
            } else if (stack.equals(ItemManager.LEAVE_JAR)) {
                player.sendMessage(Messages.PREFIX + "Du hast das Jump and Run §cverlassen§7.");
                paintWars.getItemManager().giveLobbyItems(player);
                paintWars.getLocationManager().teleport(player, "Lobby");
                player.playSound(player.getLocation(), Sound.NOTE_BASS, 1, 1);
                PlayerMoveListener.getInstance().getBegun().remove(player.getName());
                PlayerMoveListener.getInstance().getFinished().remove(player.getName());
            } else if (stack.equals(ItemManager.BACK)) {
                paintWars.getLocationManager().teleport(player, "JumpAndRun");
                player.playSound(player.getLocation(), Sound.BAT_DEATH, 1, 1);
                player.getInventory().setItem(3, ItemManager.WAIT);
                Bukkit.getScheduler().scheduleSyncDelayedTask(paintWars, () -> {
                    if (player.getInventory().getItem(3) != null && player.getInventory().getItem(3).equals(ItemManager.WAIT)) {
                        player.getInventory().setItem(3, ItemManager.BACK);
                    }
                }, 20);
            } else if (stack.equals(ItemManager.TEAM_SELECT)) {
                paintWars.getTeamInventory().show(player);
            } else if (stack.equals(ItemManager.START_GAME)) {
                player.playSound(player.getLocation(), Sound.NOTE_BASS, 1, 1);
                Bukkit.dispatchCommand(player, "start");
            }
        }
    }

}
