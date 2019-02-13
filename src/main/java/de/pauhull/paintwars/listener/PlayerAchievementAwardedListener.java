package de.pauhull.paintwars.listener;

import de.pauhull.paintwars.PaintWars;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerAchievementAwardedEvent;

/**
 * Created by Paul
 * on 08.12.2018
 *
 * @author pauhull
 */
public class PlayerAchievementAwardedListener extends ListenerTemplate {

    public PlayerAchievementAwardedListener(PaintWars paintWars) {
        super(paintWars);
    }

    @EventHandler
    public void onPlayerAchievementAwarded(PlayerAchievementAwardedEvent event) {
        event.setCancelled(true);
    }

}
