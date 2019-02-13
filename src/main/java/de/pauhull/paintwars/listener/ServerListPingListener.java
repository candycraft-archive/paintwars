package de.pauhull.paintwars.listener;

import de.pauhull.paintwars.PaintWars;
import de.pauhull.paintwars.game.Team;
import org.bukkit.event.EventHandler;
import org.bukkit.event.server.ServerListPingEvent;

/**
 * Created by Paul
 * on 11.12.2018
 *
 * @author pauhull
 */
public class ServerListPingListener extends ListenerTemplate {

    public ServerListPingListener(PaintWars paintWars) {
        super(paintWars);
    }

    @EventHandler
    public void onServerListPing(ServerListPingEvent event) {
        event.setMaxPlayers(Team.MAX_PLAYERS);
    }

}
