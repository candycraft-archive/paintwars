package de.pauhull.paintwars.util;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.java.JavaPlugin;


/**
 * Created by Paul
 * on 08.12.2018
 *
 * @author pauhull
 */
public class Trail {

    private int task;

    public Trail(JavaPlugin plugin, Entity entity, Runnable runnable) {
        this.task = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, () -> {
            if (entity == null || entity.isDead()) {
                Bukkit.getScheduler().cancelTask(task);
                return;
            }

            runnable.run();
        }, 0, 1);
    }

}
