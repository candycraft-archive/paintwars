package de.pauhull.paintwars.listener;

import de.pauhull.paintwars.PaintWars;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityChangeBlockEvent;

/**
 * Created by Paul
 * on 08.12.2018
 *
 * @author pauhull
 */
public class EntityChangeBlockListener extends ListenerTemplate {

    public EntityChangeBlockListener(PaintWars paintWars) {
        super(paintWars);
    }

    @EventHandler
    public void onBlockFall(EntityChangeBlockEvent event) {
        if (event.getEntityType() == EntityType.FALLING_BLOCK) {
            event.setCancelled(true);
        }
    }

}
