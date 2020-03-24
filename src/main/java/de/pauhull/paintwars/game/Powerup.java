package de.pauhull.paintwars.game;

import de.pauhull.paintwars.Messages;
import de.pauhull.paintwars.PaintWars;
import de.pauhull.paintwars.manager.ItemManager;
import de.pauhull.paintwars.util.ItemBuilder;
import de.pauhull.paintwars.util.Title;
import de.pauhull.utils.particle.ParticlePlayer;
import de.pauhull.utils.particle.effect.ParticleEffect;
import de.pauhull.utils.particle.effect.SpiralEffect;
import de.pauhull.utils.particle.v1_13.Particles;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Random;
import java.util.function.Consumer;

/**
 * Created by Paul
 * on 09.12.2018
 *
 * @author pauhull
 */
public enum Powerup {
    /*⬛«*/

    SPEED(Material.RABBIT_FOOT, 1, 0, "§bGeschwindigkeits-Powerup", player -> {
        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_PLING, 1, 1);
        player.sendMessage(Messages.PREFIX + "Du hast einen Geschwindigkeits-Boost erhalten!");
        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 400, 1));
    }),

    HEALTH(Material.INK_SACK, 1, 1, "§cSofortheilungs-Powerup", player -> {
        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_PLING, 1, 1);
        player.sendMessage(Messages.PREFIX + "Du bist nun unsichtbar!");
        ItemStack potionStack = new ItemBuilder(Material.POTION)
                .setPotion(PotionType.INSTANT_HEAL, false, 1, true).setDisplayName("§8⬛ §cSofortheilung §8«").build();
        player.getInventory().addItem(potionStack);
    }),

    INVISIBILITY(Material.QUARTZ, 1, 0, "§fUnsichtbarkeits-Powerup", player -> {
        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_PLING, 1, 1);
        player.sendMessage(Messages.PREFIX + "Du bist nun unsichtbar!");
        player.getInventory().setArmorContents(new ItemStack[4]);
        player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 0));
    }),

    BOMBS(Material.SNOW_BALL, 1, 0, "§9Farbbombem-Powerup", player -> {
        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_PLING, 1, 1);
        ItemStack bombs = ItemManager.BOMBS.clone();
        player.sendMessage(Messages.PREFIX + "Du hast 5 Extra-Bomben erhalten!");
        bombs.setAmount(5);
        player.getInventory().addItem(bombs);
    }),

    TANK(Material.IRON_CHESTPLATE, 1, 0, "§3Rüstungs-Powerup", player -> {
        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_PLING, 1, 1);
        player.sendMessage(Messages.PREFIX + "Du hast eine Eisenrüstung erhalten!");
        player.getInventory().setArmorContents(new ItemStack[]{
                new ItemBuilder(Material.IRON_BOOTS).setDisplayName("§8⬛ §cEisenschuhe §8«").setUnbreakable(true).build(),
                new ItemBuilder(Material.IRON_LEGGINGS).setDisplayName("§8⬛ §cEisenhose §8«").setUnbreakable(true).build(),
                new ItemBuilder(Material.IRON_CHESTPLATE).setDisplayName("§8⬛ §cEisenbrustplatte §8«").setUnbreakable(true).build(),
                new ItemBuilder(Material.IRON_HELMET).setDisplayName("§8⬛ §cEisenhelm §8«").setUnbreakable(true).build()
        });
    }),

    DISGUISE(Material.NAME_TAG, 1, 0, "§dTarnungs-Powerup", player -> {
        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_PLING, 1, 1);
        Team team = Disguises.disguise(player);
        if (team == null) {
            player.sendMessage(Messages.PREFIX + "Es wurde §ckein §7Team gefunden, als das du dich tarnen kannst.");
        } else {
            player.sendMessage(Messages.PREFIX + "Du bist nun als " + team.getColoredName() + "§7 getarnt, biss du stirbst.");
        }
    });

    private static final Random random = new Random();

    @Getter
    private static final LinkedList<Item> powerups = new LinkedList<>();

    @Getter
    private static final Map<Item, ParticleEffect> particles = new HashMap<>();

    @Getter
    private ItemStack stack;

    @Getter
    private Consumer<Player> onPickup;

    Powerup(Material material, int amount, int durability, String displayName, Consumer<Player> onPickup) {
        this.stack = new ItemBuilder(material, amount, durability).setDisplayName(displayName).build();
        this.onPickup = onPickup;
    }

    public static void updateCompass() {
        for (Team team : Team.values()) {
            for (Player player : team.getMembers()) {
                if (powerups.isEmpty()) {
                    if (player.getInventory().contains(ItemManager.POWERUP_FINDER)) {
                        player.getInventory().remove(ItemManager.POWERUP_FINDER);
                    }
                } else {
                    if (!player.getInventory().contains(ItemManager.POWERUP_FINDER)) {
                        player.getInventory().setItem(8, ItemManager.POWERUP_FINDER);
                        player.setCompassTarget(powerups.getLast().getLocation());
                    }
                }
            }
        }
    }

    public static Location pickPowerupLocation() {
        return PaintWars.getInstance().getLocationManager().getLocation("Powerup" + (random.nextInt(10) + 1));
    }

    public static void spawnPowerup() {
        Powerup powerup = values()[random.nextInt(values().length)];
        Location location = pickPowerupLocation();
        if (location != null) {
            location.getWorld().strikeLightningEffect(location);
            Item item = location.getWorld().dropItem(location, powerup.getStack());
            Bukkit.getScheduler().scheduleSyncDelayedTask(PaintWars.getInstance(), () -> {
                item.setVelocity(new Vector());
            }, 1);
            powerups.addLast(item);
            ParticleEffect effect = new SpiralEffect(PaintWars.getInstance().getScheduledExecutorService(), item.getLocation().subtract(0.5, 0, 0.5),
                    new ParticlePlayer(Particles.FLAME), 50, 1.5, 3, 10, 1.75, true, 1).play();
            particles.put(item, effect);
            PaintWars.broadcastMessage(Messages.PREFIX + "Ein §d§lPowerup §7wurde bei §e" + location.getBlockX() + "/" + location.getBlockY() + "/" + location.getBlockZ() + "§7 gespawnt!");
            for (Player player : Bukkit.getOnlinePlayers()) {
                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_PLING, 1, 1);
                Title.sendTitle(player, "§7Ein §d§lPowerup", "§7wurde gespawnt!", 0, 40, 20);
            }
            updateCompass();
        }
    }

    public void collect(Player player, Item item) {
        powerups.remove(item);
        particles.get(item).stop();
        particles.remove(item);
        PaintWars.broadcastMessage(Messages.PREFIX + "§e" + player.getName() + "§7 hat ein §d§lPowerup §7aufgesammelt!");
        onPickup.accept(player);
        updateCompass();

        PaintWars.getInstance().getStatsTable().getStats(player.getUniqueId(), stats -> {
            if (stats == null) return;
            stats.setCollectedPowerups(stats.getCollectedPowerups() + 1);
            PaintWars.getInstance().getStatsTable().setStats(player.getUniqueId(), stats);
        });
    }

}
