package de.pauhull.paintwars.listener;

import de.pauhull.paintwars.PaintWars;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;

/**
 * Created by Paul
 * on 06.12.2018
 *
 * @author pauhull
 */
public abstract class ListenerTemplate implements Listener {

    protected PaintWars paintWars;

    public ListenerTemplate(PaintWars paintWars) {
        this.paintWars = paintWars;

        Bukkit.getPluginManager().registerEvents(this, paintWars);
    }

}
