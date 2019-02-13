package de.pauhull.paintwars.listener;

import de.pauhull.nickapi.event.PostPlayerNickEvent;
import de.pauhull.nickapi.event.PostPlayerUnnickEvent;
import de.pauhull.paintwars.PaintWars;
import org.bukkit.event.EventHandler;

/**
 * Created by Paul
 * on 13.12.2018
 *
 * @author pauhull
 */
public class PlayerNickListener extends ListenerTemplate {

    public PlayerNickListener(PaintWars paintWars) {
        super(paintWars);
    }

    @EventHandler
    public void onPostPlayerNick(PostPlayerNickEvent event) {
        paintWars.getScoreboardManager().updateTeam(event.getPlayer());
    }

    @EventHandler
    public void onPostPlayerUnnick(PostPlayerUnnickEvent event) {
        paintWars.getScoreboardManager().updateTeam(event.getPlayer());
    }

}
