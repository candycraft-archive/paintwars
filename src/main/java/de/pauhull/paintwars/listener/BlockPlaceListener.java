package de.pauhull.paintwars.listener;

import de.pauhull.paintwars.PaintWars;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockPlaceEvent;

/**
 * Created by Paul
 * on 07.12.2018
 *
 * @author pauhull
 */
public class BlockPlaceListener extends ListenerTemplate {

    public BlockPlaceListener(PaintWars paintWars) {
        super(paintWars);
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        event.setCancelled(true);
    }

}
