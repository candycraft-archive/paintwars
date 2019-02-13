package de.pauhull.paintwars;

import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;

/**
 * Created by Paul
 * on 07.12.2018
 *
 * @author pauhull
 */
public class Permissions {

    public static final String SETLOCATION = "paintwars.setlocation";
    public static final String START = "paintwars.start";

    public static final PluginManager pm() {
        return Bukkit.getPluginManager();
    }

}
