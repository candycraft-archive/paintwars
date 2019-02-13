package de.pauhull.paintwars.listener;

import de.pauhull.paintwars.PaintWars;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerDropItemEvent;

/**
 * Created by Paul
 * on 07.12.2018
 *
 * @author pauhull
 */
public class PlayerDropItemListener extends ListenerTemplate {

    public PlayerDropItemListener(PaintWars paintWars) {
        super(paintWars);
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        event.setCancelled(true);
    }

}
