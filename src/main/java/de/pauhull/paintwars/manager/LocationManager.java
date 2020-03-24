package de.pauhull.paintwars.manager;

import de.dytanic.cloudnet.bridge.CloudServer;
import de.pauhull.paintwars.PaintWars;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.Random;

/**
 * Created by Paul
 * on 07.12.2018
 *
 * @author pauhull
 */
public class LocationManager {

    private YamlConfiguration config;
    private File file;

    public LocationManager(PaintWars paintWars) {

        File locationsFolder = new File(paintWars.getDataFolder(), "locations/");

        if (!locationsFolder.exists()) {
            locationsFolder.mkdirs();
        }

        File[] worlds = locationsFolder.listFiles(file -> file.getName().endsWith(".yml"));

        if (worlds == null || worlds.length == 0) {
            Bukkit.shutdown();
            return;
        }

        this.file = worlds[new Random().nextInt(worlds.length)];
        CloudServer.getInstance().setMotdAndUpdate(file.getName().split("\\.")[0]);
        config = YamlConfiguration.loadConfiguration(file);
    }

    public void saveLocation(Location location, String name) {

        config.set(name, location);

        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Location getLocation(String name) {

        ConfigurationSection section = config.getConfigurationSection(name);

        String worldName = section.getString("world");
        World world = Bukkit.getWorld(worldName);

        if (world == null) {
            world = Bukkit.createWorld(new WorldCreator(worldName));
        }

        return new Location(world,
                section.getDouble("x"),
                section.getDouble("y"),
                section.getDouble("z"),
                (float) section.getDouble("yaw"),
                (float) section.getDouble("pitch"));
    }

    public boolean isSet(String name) {

        return config.isSet(name);
    }

    public void teleport(Player player, String name) {

        if (isSet(name)) {
            player.teleport(getLocation(name));
        }
    }
}
