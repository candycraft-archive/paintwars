package de.pauhull.paintwars.listener;

import de.pauhull.paintwars.PaintWars;
import de.pauhull.paintwars.game.Team;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

/**
 * Created by Paul
 * on 08.12.2018
 *
 * @author pauhull
 */
public class EntityDamageByEntityListener extends ListenerTemplate {

    public EntityDamageByEntityListener(PaintWars paintWars) {
        super(paintWars);
    }

    @EventHandler
    public void onEntityByDamageEntity(EntityDamageByEntityEvent event) {

        if (event.getDamager() instanceof Snowball && event.getEntity() instanceof Player) {
            Snowball snowball = (Snowball) event.getDamager();
            Player damaged = (Player) event.getEntity();

            if (snowball.getShooter() instanceof Player) {
                Player damager = (Player) snowball.getShooter();

                Team damagedTeam = Team.getTeam(damaged);
                Team damagerTeam = Team.getTeam(damager);

                if (damagerTeam == null || damagedTeam == null) {
                    event.setCancelled(true);
                    event.setDamage(0);
                    return;
                }

                if (damagedTeam == damagerTeam) {
                    event.setCancelled(true);
                    event.setDamage(0);
                    return;
                }
            }
        }

        if (event.getDamager() instanceof Player && event.getEntity() instanceof Player) {
            Player damaged = (Player) event.getEntity();
            Player damager = (Player) event.getDamager();

            Team damagedTeam = Team.getTeam(damaged);
            Team damagerTeam = Team.getTeam(damager);

            if (damagerTeam == null || damagedTeam == null) {
                event.setCancelled(true);
                event.setDamage(0);
                return;
            }

            if (damagedTeam == damagerTeam) {
                event.setCancelled(true);
                event.setDamage(0);
                return;
            }
        }
    }
}
