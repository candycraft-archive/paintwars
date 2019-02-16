package de.pauhull.paintwars.listener;

import de.pauhull.paintwars.PaintWars;
import de.pauhull.paintwars.game.Team;
import de.pauhull.paintwars.phase.GamePhase;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.AsyncPlayerChatEvent;

/**
 * Created by Paul
 * on 07.12.2018
 *
 * @author pauhull
 */
public class AsyncPlayerChatListener extends ListenerTemplate {

    public AsyncPlayerChatListener(PaintWars paintWars) {
        super(paintWars);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onAsyncPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        Team team = Team.getTeam(player);

        if (paintWars.getSpectators().contains(player)) {
            for (Player spectator : paintWars.getSpectators()) {
                spectator.sendMessage("§8[§4✘§8] §r" + event.getFormat());
            }
            event.setFormat("");
            event.setCancelled(true);
            return;
        }

        if (paintWars.getPhaseHandler().getActivePhaseType() == GamePhase.Type.INGAME) {
            boolean global = event.getMessage().startsWith("@a ");
            if (global) {
                event.setFormat(event.getFormat().replaceFirst("@a ", ""));
            }

            if (team != null) {
                if (global) {
                    event.setFormat("§8[§7@a§8] " + team.getChatColor() + "[" + team.getColoredName() + "] §r" + event.getFormat());
                } else {
                    for (Player sendTo : team.getMembers()) {
                        sendTo.sendMessage("§8[" + team.getChatColor() + "Teamchat§8] §r" + event.getFormat());
                    }
                    event.setFormat("");
                    event.setCancelled(true);
                }
            }
        } else {
            if (team != null) {
                event.setFormat(team.getChatColor() + "[" + team.getColoredName() + "] §r" + event.getFormat());
            }
        }
    }

}
