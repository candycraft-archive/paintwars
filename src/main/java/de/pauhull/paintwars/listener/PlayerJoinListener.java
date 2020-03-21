package de.pauhull.paintwars.listener;

import de.pauhull.paintwars.Messages;
import de.pauhull.paintwars.PaintWars;
import de.pauhull.paintwars.data.table.StatsTable;
import de.pauhull.paintwars.game.Team;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Paul
 * on 07.12.2018
 *
 * @author pauhull
 */
public class PlayerJoinListener extends ListenerTemplate {

    public PlayerJoinListener(PaintWars paintWars) {
        super(paintWars);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerJoin(PlayerJoinEvent event) {

        Player player = event.getPlayer();

        if (!paintWars.getColoredBlocks().containsKey(player.getUniqueId())) {
            paintWars.getColoredBlocks().put(player.getUniqueId(), new AtomicInteger());
        }

        paintWars.getStatsTable().getStats(player.getUniqueId(), stats -> {
            if (stats == null) {
                paintWars.getStatsTable().setStats(player.getUniqueId(), StatsTable.Stats.getDefault());
            }
        });

        switch (paintWars.getPhaseHandler().getActivePhaseType()) {
            case LOBBY:
                event.setJoinMessage(Messages.PREFIX + "§e" + player.getName() + "§7 ist dem Spiel §abeigetreten§7! §8[§e"
                        + Bukkit.getOnlinePlayers().size() + "§8/§e" + Team.MAX_PLAYERS + "§8]");
                paintWars.getItemManager().giveLobbyItems(player);
                paintWars.getLocationManager().teleport(player, "Lobby");
                player.setLevel(0);
                player.setGameMode(GameMode.ADVENTURE);
                player.setFoodLevel(20);
                player.setHealth(20.0);
                player.setExp(0);

                for (Player all : Bukkit.getOnlinePlayers()) {
                    all.showPlayer(player);
                }

                break;
            case ENDING:
            case INGAME:
                event.setJoinMessage(null);
                Location location = paintWars.getLocationManager().getLocation("BLUE");
                if (location != null) {
                    player.teleport(location.getWorld().getSpawnLocation());
                    player.setGameMode(GameMode.ADVENTURE);
                    player.setAllowFlight(true);
                    player.setFlying(true);
                    paintWars.getItemManager().giveSpectatorItems(player);
                    paintWars.getScoreboardManager().updateTeam(player);
                    paintWars.getSpectators().add(player);

                    for (Player online : Bukkit.getOnlinePlayers()) {
                        online.hidePlayer(player);
                    }
                }
                break;
        }

        for (Player spectator : paintWars.getSpectators()) {
            player.hidePlayer(spectator);
        }
    }

}
