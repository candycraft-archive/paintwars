package de.pauhull.paintwars.listener;

import de.pauhull.paintwars.PaintWars;
import org.bukkit.event.EventHandler;
import org.bukkit.event.weather.WeatherChangeEvent;

/**
 * Created by Paul
 * on 07.12.2018
 *
 * @author pauhull
 */
public class WeatherChangeListener extends ListenerTemplate {

    public WeatherChangeListener(PaintWars paintWars) {
        super(paintWars);
    }

    @EventHandler
    public void onWeatherChange(WeatherChangeEvent event) {
        if (event.toWeatherState())
            event.setCancelled(true);
    }

}
