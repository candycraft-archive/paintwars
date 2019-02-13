package de.pauhull.paintwars.manager;

import de.pauhull.paintwars.PaintWars;
import de.pauhull.paintwars.util.TimedHashMap;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * Created by Paul
 * on 07.12.2018
 *
 * @author pauhull
 */
public class LocationManager {

    private TimedHashMap<String, Location> cache = new TimedHashMap<>(TimeUnit.MINUTES, 5);
    private PaintWars paintWars;
    private YamlConfiguration config;
    private File file;

    public LocationManager(PaintWars paintWars) {
        this.paintWars = paintWars;
        this.file = new File(paintWars.getDataFolder(), "locations.yml");
        this.config = YamlConfiguration.loadConfiguration(file);
    }

    public void saveLocation(Location location, String name) {
        config.set(name, location);
        cache.put(name, location);

        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Location getLocation(String name) {
        if (cache.containsKey(name)) {
            return cache.get(name);
        }

        Location location = (Location) config.get(name);
        cache.put(name, location);
        return location;
    }

    public boolean isSet(String name) {
        if (cache.containsKey(name)) {
            return true;
        }

        return config.isSet(name);
    }

    public void teleport(Player player, String name) {
        if (isSet(name)) {
            player.teleport(getLocation(name));
        }
    }

}
