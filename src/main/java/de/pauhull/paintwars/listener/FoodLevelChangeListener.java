package de.pauhull.paintwars.listener;

import de.pauhull.paintwars.PaintWars;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.FoodLevelChangeEvent;

/**
 * Created by Paul
 * on 07.12.2018
 *
 * @author pauhull
 */
public class FoodLevelChangeListener extends ListenerTemplate {

    public FoodLevelChangeListener(PaintWars paintWars) {
        super(paintWars);
    }

    @EventHandler
    public void onFoodLevelChange(FoodLevelChangeEvent event) {
        event.setFoodLevel(20);
        event.setCancelled(true);
    }

}
