package de.pauhull.paintwars.manager;

import de.pauhull.paintwars.PaintWars;
import de.pauhull.paintwars.game.Powerup;
import de.pauhull.paintwars.game.Team;
import de.pauhull.paintwars.util.ItemBuilder;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.util.Vector;

/**
 * Created by Paul
 * on 07.12.2018
 *
 * @author pauhull
 */
public class ItemManager {

    public static final ItemStack SWORD = new ItemBuilder(Material.WOOD_SWORD).setDisplayName("§aSchwert").setUnbreakable(true).addEnchant(Enchantment.DAMAGE_ALL, 1, true).addEnchant(Enchantment.KNOCKBACK, 1, true).addItemFlag(ItemFlag.HIDE_ENCHANTS).build();
    public static final ItemStack LEAVE = new ItemBuilder(Material.SLIME_BALL).setDisplayName("§eZur Lobby §7§o<Rechtsklick>").build();
    public static final ItemStack TEAM_SELECT = new ItemBuilder(Material.BOOK).setDisplayName("§bTeam auswählen §7§o<Rechtsklick>").build();
    public static final ItemStack BACK = new ItemBuilder(Material.INK_SACK, 1, 1).setDisplayName("§cZurück §7§o<Rechtsklick>").build();
    public static final ItemStack LEAVE_JAR = new ItemBuilder(Material.MAGMA_CREAM).setDisplayName("§eJump and Run verlassen §7§o<Rechtsklick>").build();
    public static final ItemStack WAIT = new ItemBuilder(Material.INK_SACK, 1, 8).setDisplayName("§7Bitte warten...").build();
    public static final ItemStack BOMBS = new ItemBuilder(Material.SNOW_BALL).setDisplayName("§9Farbbombe").build();
    public static final ItemStack SPECTATOR = new ItemBuilder(Material.COMPASS).setDisplayName("§cSpieler zuschauen §7§o<Rechtsklick>").build();
    public static final ItemStack POWERUP_FINDER = new ItemBuilder(Material.COMPASS).setDisplayName("§dPowerup-Finder §7§o<Rechtsklick>").build();
    public static final ItemStack START_GAME = new ItemBuilder(Material.FEATHER).setDisplayName("§aSpiel starten §7§o<Rechtsklick>").build();

    private PaintWars paintWars;

    public ItemManager(PaintWars paintWars) {
        this.paintWars = paintWars;
    }

    public void giveLobbyItems(Player player) {
        player.getInventory().clear();
        player.getInventory().setItem(2, START_GAME);
        player.getInventory().setItem(4, LEAVE);
        player.getInventory().setItem(6, TEAM_SELECT);
    }

    public void giveJumpAndRunItems(Player player) {
        player.getInventory().clear();
        player.getInventory().setItem(3, BACK);
        player.getInventory().setItem(5, LEAVE_JAR);
    }

    public void giveSpectatorItems(Player player) {
        player.getInventory().clear();
        player.getInventory().setHelmet(null);
        player.getInventory().setLeggings(null);
        player.getInventory().setChestplate(null);
        player.getInventory().setBoots(null);
        player.setLevel(0);
        player.setExp(0);
        player.getInventory().setItem(4, SPECTATOR);
    }

    public void giveIngameItems(Player player) {
        Team team = Team.getTeam(player);
        player.getInventory().clear();
        player.setLevel(0);
        player.setExp(0);
        player.setGameMode(GameMode.SURVIVAL);
        player.setVelocity(new Vector());

        if (team != null) {
            team.giveArmor(player);
        }

        for (PotionEffect potion : player.getActivePotionEffects()) {
            player.removePotionEffect(potion.getType());
        }

        player.getInventory().setItem(0, SWORD);
        player.getInventory().setItem(1, BOMBS);

        if (!Powerup.getPowerups().isEmpty()) {
            player.getInventory().setItem(8, POWERUP_FINDER);
        }

        player.updateInventory();
    }

}
