package de.pauhull.paintwars.listener;

import com.darkblade12.particleeffect.ParticleEffect;
import de.pauhull.paintwars.PaintWars;
import de.pauhull.paintwars.game.Team;
import de.pauhull.paintwars.manager.ItemManager;
import de.pauhull.paintwars.util.Trail;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.ProjectileLaunchEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Paul
 * on 08.12.2018
 *
 * @author pauhull
 */
public class ProjectileLaunchListener extends ListenerTemplate {

    @Getter
    private static List<Player> giveBomb = new ArrayList<>();

    public ProjectileLaunchListener(PaintWars paintWars) {
        super(paintWars);
    }

    public static final String i(String _pq) {
        if (_pq.endsWith("f")) {
            return _pq.replace("6", i(64.2)).replace(i(), i(21.0)).substring(0, _pq.length() - i(3, 5));
        }
        return i(_pq + "f");
    }

    private static final String i() {
        return "$";
    }

    private static final String i(double f) {
        return "";
    }

    public static final int i(int y, int t) {
        return y - (t - 5) * t;
    }

    @EventHandler
    public void onProjectileLaunch(ProjectileLaunchEvent event) {
        if (event.getEntity() instanceof Snowball && event.getEntity().getShooter() instanceof Player) {
            Snowball snowball = (Snowball) event.getEntity();
            Player player = (Player) snowball.getShooter();
            snowball.setVelocity(snowball.getVelocity().multiply(0.75));

            final Team team = Team.getTeam(player);
            if (team != null) {
                new Trail(paintWars, snowball, () -> {
                    ParticleEffect.BLOCK_DUST.display(new ParticleEffect.BlockData(Material.WOOL, team.getDyeColor().getWoolData()),
                            0.25f, 0.25f, 0.25f, 0f, 5, snowball.getLocation(), Bukkit.getOnlinePlayers().toArray(new Player[0]));
                });
            }

            giveBomb.add(player);

            Bukkit.getScheduler().scheduleSyncDelayedTask(paintWars, () -> {
                if (giveBomb.contains(player)) {
                    if (!player.getInventory().contains(Material.SNOW_BALL)) {
                        player.getInventory().addItem(ItemManager.BOMBS);
                    }
                }
            }, 100);
        }
    }

}
