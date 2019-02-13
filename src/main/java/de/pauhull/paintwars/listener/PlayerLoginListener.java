package de.pauhull.paintwars.listener;

import de.pauhull.paintwars.Messages;
import de.pauhull.paintwars.PaintWars;
import de.pauhull.paintwars.game.Team;
import de.pauhull.paintwars.phase.GamePhase;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerLoginEvent;

/**
 * Created by Paul
 * on 07.12.2018
 *
 * @author pauhull
 */
public class PlayerLoginListener extends ListenerTemplate {

    public PlayerLoginListener(PaintWars paintWars) {
        super(paintWars);
    }

    @EventHandler
    public void onLogin(PlayerLoginEvent event) {
        if (paintWars.getPhaseHandler().getActivePhaseType() == GamePhase.Type.LOBBY) {
            if (Bukkit.getOnlinePlayers().size() > Team.MAX_PLAYERS) {
                event.setResult(PlayerLoginEvent.Result.KICK_FULL);
                event.setKickMessage(Messages.PREFIX + "Der Server ist §cvoll§7!");
            }
        }
    }

}
