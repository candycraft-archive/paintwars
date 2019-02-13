package de.pauhull.paintwars.listener;

import de.pauhull.friends.spigot.event.PlayerJoinPartyEvent;
import de.pauhull.friends.spigot.event.PlayerLeavePartyEvent;
import de.pauhull.paintwars.PaintWars;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

/**
 * Created by Paul
 * on 05.01.2019
 *
 * @author pauhull
 */
public class PlayerPartyListener extends ListenerTemplate {

    public PlayerPartyListener(PaintWars paintWars) {
        super(paintWars);
    }

    @EventHandler
    public void onPlayerJoinParty(PlayerJoinPartyEvent event) {
        paintWars.getScoreboardManager().updateTeam(event.getPlayer());
    }

    @EventHandler
    public void onPlayerLeaveParty(PlayerLeavePartyEvent event) {
        this.paintWars.getScoreboardManager().updateTeam(event.getPlayer());
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (event.getParty().getMembers().contains(player.getName())) {
                this.paintWars.getScoreboardManager().updateTeam(player);
            }
        }
    }

}
