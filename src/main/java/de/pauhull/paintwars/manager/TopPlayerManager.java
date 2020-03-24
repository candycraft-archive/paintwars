package de.pauhull.paintwars.manager;

import de.pauhull.paintwars.PaintWars;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;

/**
 * Created by Paul
 * on 23.02.2019
 *
 * @author pauhull
 */
public class TopPlayerManager {

    private PaintWars paintWars;
    private File skinCacheFile;
    private FileConfiguration skinCache;

    public TopPlayerManager(PaintWars paintWars) {
        this.paintWars = paintWars;
        this.skinCacheFile = new File(System.getProperty("user.home"), "skinCache.yml");
        if (!this.skinCacheFile.exists()) {
            try {
                skinCacheFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        this.skinCache = YamlConfiguration.loadConfiguration(skinCacheFile);
        this.init();
    }

    public void init() {
        if (!paintWars.getLocationManager().isSet("top1")
                || !paintWars.getLocationManager().isSet("top2")
                || !paintWars.getLocationManager().isSet("top3")
                || !paintWars.getLocationManager().isSet("sign1")
                || !paintWars.getLocationManager().isSet("sign2")
                || !paintWars.getLocationManager().isSet("sign3"))
            return;

        paintWars.getStatsTable().getTopPlayers(3, topPlayers -> {
            if (topPlayers.size() != 3) return;

            for (int i = 0; i < 3; i++) {
                final Location location = paintWars.getLocationManager().getLocation("top" + (i + 1));
                final Location signLocation = paintWars.getLocationManager().getLocation("sign" + (i + 1));
                final int index = i;

                paintWars.getUuidFetcher().fetchProfile(topPlayers.get(i).toString(), (uuid, name) -> {
                    //Npc npc = new Npc(location, UUID.randomUUID(), null, getSkinData(uuid), false);
                    //npc.spawn();

                    final DecimalFormat format = new DecimalFormat("#.##");
                    paintWars.getStatsTable().getStats(uuid, stats -> Bukkit.getScheduler().runTask(paintWars, () -> {
                        Block block = signLocation.getBlock();
                        Sign sign = (Sign) block.getState();
                        sign.setLine(0, "§2" + (index + 1) + ".");
                        sign.setLine(1, "§1" + name);
                        sign.setLine(2, "§4" + stats.getWins() + " Wins");
                        sign.setLine(3, "§5K/D: " + format.format(stats.getKD()));
                        sign.update();
                    }));
                });
            }
        });
    }

    /*
    public SkinData getSkinData(UUID uuid) { // Cache skin for a hour
        if (skinCache.isSet(uuid.toString())) {
            if (System.currentTimeMillis() - skinCache.getLong(uuid + ".Timestamp") > TimeUnit.HOURS.toMillis(1)) {
                skinCache.set(uuid.toString(), null);
                try {
                    skinCache.save(skinCacheFile);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return getSkinData(uuid);
            } else {
                String texture = skinCache.getString(uuid + ".Texture");
                String signature = skinCache.getString(uuid + ".Signature");
                return new SkinData(texture, signature);
            }
        }

        SkinData data = SkinData.getSkinSync(uuid);
        if (data == null) return null;

        skinCache.set(uuid + ".Timestamp", System.currentTimeMillis());
        skinCache.set(uuid + ".Texture", data.getTexture());
        skinCache.set(uuid + ".Signature", data.getSignature());
        try {
            skinCache.save(skinCacheFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return data;
    }
     */

}
