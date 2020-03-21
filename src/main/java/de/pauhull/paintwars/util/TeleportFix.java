package de.pauhull.paintwars.util;

import net.minecraft.server.v1_12_R1.*;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_12_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;

/**
 * https://gist.github.com/aadnk/3773860
 */
public class TeleportFix implements Listener {

    // Try increasing this. May be dependent on lag.
    private final int TELEPORT_FIX_DELAY = 15; // ticks
    private Server server;
    private Plugin plugin;

    public TeleportFix(Plugin plugin, Server server) {
        this.plugin = plugin;
        this.server = server;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerTeleport(PlayerTeleportEvent event) {

        final Player player = event.getPlayer();
        final int visibleDistance = server.getViewDistance() * 16;

        // Fix the visibility issue one tick later
        server.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
            // Refresh nearby clients
            updateEntities(getPlayersWithin(player, visibleDistance));

            System.out.println("Applying fix ... " + visibleDistance);
        }, TELEPORT_FIX_DELAY);
    }


    public void updateEntities(List<Player> observers) {

        // Refresh every single player
        for (Player player : observers) {
            updateEntity(player, observers);
        }
    }

    public void updateEntity(Entity entity, List<Player> observers) {

        World world = entity.getWorld();
        WorldServer worldServer = ((CraftWorld) world).getHandle();

        EntityTracker tracker = worldServer.tracker;
        EntityTrackerEntry entry = tracker.trackedEntities
                .get(entity.getEntityId());

        List<EntityPlayer> nmsPlayers = getNmsPlayers(observers);

        // Force Minecraft to resend packets to the affected clients
        entry.trackedPlayers.removeAll(nmsPlayers);
        entry.scanPlayers((List<EntityHuman>) (Object) nmsPlayers);
    }

    private List<EntityPlayer> getNmsPlayers(List<Player> players) {
        List<EntityPlayer> nsmPlayers = new ArrayList<>();

        for (Player bukkitPlayer : players) {
            CraftPlayer craftPlayer = (CraftPlayer) bukkitPlayer;
            nsmPlayers.add(craftPlayer.getHandle());
        }

        return nsmPlayers;
    }

    private List<Player> getPlayersWithin(Player player, int distance) {
        List<Player> res = new ArrayList<>();
        int d2 = distance * distance;

        for (Player p : server.getOnlinePlayers()) {
            if (p.getWorld() == player.getWorld()
                    && p.getLocation().distanceSquared(player.getLocation()) <= d2) {
                res.add(p);
            }
        }

        return res;
    }
}