package de.pauhull.paintwars.listener;

import de.pauhull.paintwars.PaintWars;
import de.pauhull.paintwars.phase.GamePhase;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;

/**
 * Created by Paul
 * on 07.12.2018
 *
 * @author pauhull
 */
public class InventoryClickListener extends ListenerTemplate {

    public InventoryClickListener(PaintWars paintWars) {
        super(paintWars);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        paintWars.getBuyItemInventory().onInventoryClick(event);

        if (paintWars.getSpectators().contains(event.getWhoClicked())) {
            event.setCancelled(true);
            return;
        }

        if (paintWars.getPhaseHandler().getActivePhaseType() == GamePhase.Type.LOBBY) {
            event.setCancelled(true);
        }
    }

}
