package de.pauhull.paintwars.listener;

import de.godtitan.coins.CoinAPI;
import de.pauhull.paintwars.Messages;
import de.pauhull.paintwars.PaintWars;
import de.pauhull.paintwars.game.Disguises;
import de.pauhull.paintwars.game.Team;
import de.pauhull.paintwars.phase.GamePhase;
import de.pauhull.paintwars.util.Title;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

/**
 * Created by Paul
 * on 08.12.2018
 *
 * @author pauhull
 */
public class PlayerDeathListener extends ListenerTemplate {

    public PlayerDeathListener(PaintWars paintWars) {
        super(paintWars);
    }

    @EventHandler
    public void onPlayerDamage(EntityDamageEvent event) {

        double damage = event.getDamage();

        if (!(event.getEntity() instanceof Player)) {
            return;
        }
        Player player = (Player) event.getEntity();

        if (player.getHealth() - damage > 1.0) {
            return;
        }

        player.setHealth(20);

        Player temp = null;
        if (event instanceof EntityDamageByEntityEvent && ((EntityDamageByEntityEvent) event).getDamager() instanceof Player) {
            temp = (Player) ((EntityDamageByEntityEvent) event).getDamager();
        }
        final Player killer = temp;

        Disguises.undisguise(player);

        if (paintWars.getPhaseHandler().getActivePhaseType() != GamePhase.Type.INGAME) {
            player.setHealth(20.0);
            player.setFoodLevel(20);
            return;
        }

        paintWars.getStatsTable().getStats(player.getUniqueId(), stats -> {
            if (stats == null) return;
            stats.setDeaths(stats.getDeaths() + 1);
            paintWars.getStatsTable().setStats(player.getUniqueId(), stats);
        });

        Team team = Team.getTeam(player);
        ChatColor prefix = ChatColor.YELLOW;
        if (team != null) {
            prefix = team.getChatColor();
        }


        if (killer != null) {
            paintWars.getStatsTable().getStats(killer.getUniqueId(), stats -> {
                if (stats == null) return;
                stats.setKills(stats.getKills() + 1);
                paintWars.getStatsTable().setStats(killer.getUniqueId(), stats);
            });

            Team killerTeam = Team.getTeam(killer);
            ChatColor prefixColor = ChatColor.RED;
            if (killerTeam != null) {
                prefixColor = killerTeam.getChatColor();
            }

            Bukkit.broadcastMessage(Messages.PREFIX + "Der Spieler " + prefix + player.getName() + "§7 wurde von " + prefixColor + killer.getName() + "§7 getötet!");
            killer.playSound(killer.getLocation(), Sound.SUCCESSFUL_HIT, 1, 1);
            Title.sendTitle(killer, "§a✔ §8» §7" + player.getName(), "§a+§75 Coins", 1, 40, 20);
            CoinAPI.getInstance().addCoins(player.getUniqueId(), 5);
            killer.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 60, 5));
        } else {
            Bukkit.broadcastMessage(Messages.PREFIX + "Der Spieler " + prefix + player.getName() + "§7 ist §cgestorben§7!");
            Title.sendTitle(player, "§4✖", "§7Du bist §cgestorben", 1, 40, 20);
        }

        respawn(player);
    }

    public void respawn(Player player) {

        ProjectileLaunchListener.getGiveBomb().remove(player);

        Team team = Team.getTeam(player);
        if (team != null) {
            paintWars.getLocationManager().teleport(player, team.name());
            paintWars.getItemManager().giveIngameItems(player);
            player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, Integer.MAX_VALUE, 1));
            player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 60, 10));
            Bukkit.getScheduler().scheduleSyncDelayedTask(PaintWars.getInstance(), () -> {
                player.removePotionEffect(PotionEffectType.BLINDNESS);
            }, 10);
        }
        for (Player all : Bukkit.getOnlinePlayers()) {
            if (!all.canSee(player)) {
                all.showPlayer(player);
            }
        }
        Bukkit.getScheduler().scheduleSyncDelayedTask(PaintWars.getInstance(), () -> {
            player.setVelocity(new Vector());
        }, 1);
    }

}
