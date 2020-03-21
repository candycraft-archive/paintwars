package de.pauhull.paintwars.inventory;

import de.pauhull.paintwars.Messages;
import de.pauhull.paintwars.PaintWars;
import de.pauhull.paintwars.game.Team;
import de.pauhull.paintwars.util.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Paul
 * on 07.12.2018
 *
 * @author pauhull
 */
public class TeamInventory implements Listener {

    private static final String TITLE = "§cTeam auswählen";
    private static final ItemStack BLACK_GLASS = new ItemBuilder(Material.STAINED_GLASS_PANE, 1, 15).setDisplayName(" ").build();

    private PaintWars paintWars;

    public TeamInventory(PaintWars paintWars) {
        this.paintWars = paintWars;

        Bukkit.getPluginManager().registerEvents(this, paintWars);
    }

    public void show(Player player) {
        Inventory inventory = Bukkit.createInventory(null, 27, TITLE);
        for (int i = 0; i < inventory.getSize(); i++) {
            inventory.setItem(i, BLACK_GLASS);
        }

        placeTeam(player, inventory, 10, Team.RED);
        placeTeam(player, inventory, 12, Team.GREEN);
        placeTeam(player, inventory, 14, Team.BLUE);
        placeTeam(player, inventory, 16, Team.YELLOW);

        player.playSound(player.getLocation(), Sound.ENTITY_ITEM_PICKUP, 1, 1);
        player.openInventory(inventory);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        Inventory inventory = event.getClickedInventory();
        ItemStack stack = event.getCurrentItem();

        if (inventory == null || inventory.getTitle() == null || !inventory.getTitle().equals(TITLE)) {
            return;
        } else {
            event.setCancelled(true);
        }

        if (stack != null) {
            if (stack.getType() == Material.WOOL) {
                if (stack.getDurability() == Team.BLUE.getDyeColor().getWoolData()) {
                    addToTeam(player, Team.BLUE);
                } else if (stack.getDurability() == Team.GREEN.getDyeColor().getWoolData()) {
                    addToTeam(player, Team.GREEN);
                } else if (stack.getDurability() == Team.YELLOW.getDyeColor().getWoolData()) {
                    addToTeam(player, Team.YELLOW);
                } else if (stack.getDurability() == Team.RED.getDyeColor().getWoolData()) {
                    addToTeam(player, Team.RED);
                }
            }
        }
    }

    private void placeTeam(Player player, Inventory inventory, int slot, Team team) {
        ItemBuilder builder = new ItemBuilder(Material.WOOL, 1, team.getDyeColor().getWoolData()).setDisplayName("§8» §r" + team.getColoredName());
        if (team.getMembers().isEmpty()) {
            builder.setLore("§8➥ §7Leer");
        } else {
            List<String> lore = new ArrayList<>();
            for (Player member : team.getMembers()) {
                lore.add("§8• §7" + member.getName());
            }
            if (team.getMembers().size() >= Team.TEAM_SIZE) {
                lore.add("§c§lVoll");
            }
            builder.setLore(lore);
        }


        ItemStack glass;

        if (team.getMembers().contains(player)) {
            glass = new ItemBuilder(Material.STAINED_GLASS_PANE, 1, team.getGlassPaneColor())
                    .setDisplayName(" ").addEnchant(Enchantment.DURABILITY, 0, true).addItemFlag(ItemFlag.HIDE_ENCHANTS).build();
            builder.addEnchant(Enchantment.DURABILITY, 0, true);
            builder.addItemFlag(ItemFlag.HIDE_ENCHANTS);
        } else {
            glass = new ItemBuilder(Material.STAINED_GLASS_PANE, 1, team.getGlassPaneColor())
                    .setDisplayName(" ").build();
        }

        inventory.setItem(slot - 9, glass);
        inventory.setItem(slot, builder.build());
        inventory.setItem(slot + 9, glass);
    }

    private void addToTeam(Player player, Team team) {
        Team currentTeam = Team.getTeam(player);

        if (team.getMembers().size() >= Team.TEAM_SIZE) {
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BASS, 1, 1);
            player.sendMessage(Messages.PREFIX + "Dieses Team ist bereits §cvoll§7!");
            return;
        }

        if (currentTeam != null) {
            if (currentTeam == team) {
                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BASS, 1, 1);
                player.sendMessage(Messages.PREFIX + "Du bist bereits in Team " + team.getColoredName() + "§7!");
                return;
            }

            currentTeam.getMembers().remove(player);
        }

        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_PLING, 1, 1);
        player.closeInventory();
        player.sendMessage(Messages.PREFIX + "Du hast das Team " + team.getColoredName() + "§7 ausgewählt!");
        team.getMembers().add(player);
        team.giveArmor(player);
        paintWars.getScoreboardManager().updateTeam(player);
    }

}
