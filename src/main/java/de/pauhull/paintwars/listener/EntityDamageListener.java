package de.pauhull.paintwars.listener;

import de.pauhull.paintwars.PaintWars;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;

/**
 * Created by Paul
 * on 07.12.2018
 *
 * @author pauhull
 */
public class EntityDamageListener extends ListenerTemplate {

    public EntityDamageListener(PaintWars paintWars) {
        super(paintWars);
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {

        if (paintWars.getSpectators().contains(event.getEntity())) {
            event.setDamage(0);
            event.setCancelled(true);
            return;
        }

        switch (paintWars.getPhaseHandler().getActivePhaseType()) {
            case LOBBY:
            case ENDING:
                event.setCancelled(true);
                event.setDamage(0);
                break;
            case INGAME:
                if (event.getCause() == EntityDamageEvent.DamageCause.FALL) {
                    event.setCancelled(true);
                    event.setDamage(0);
                }
                break;
        }
    }

}
