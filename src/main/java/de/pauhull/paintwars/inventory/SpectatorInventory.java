package de.pauhull.paintwars.inventory;

import de.pauhull.paintwars.PaintWars;
import de.pauhull.paintwars.game.Team;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Paul
 * on 07.12.2018
 *
 * @author pauhull
 */
public class SpectatorInventory implements Listener {

    private static final String TITLE = "§cSpieler zuschauen";

    private PaintWars paintWars;

    public SpectatorInventory(PaintWars paintWars) {
        this.paintWars = paintWars;

        Bukkit.getPluginManager().registerEvents(this, paintWars);
    }

    public void show(Player player) {

        if (!paintWars.getSpectators().contains(player))
            return;

        List<Player> players = new ArrayList<>();
        for (Team team : Team.values()) {
            players.addAll(team.getMembers());
        }

        Inventory inventory = Bukkit.createInventory(null, (int) Math.ceil(players.size() / 9.0) * 9, TITLE);
        int slot = 0;
        for (Player member : players) {
            ItemStack stack = paintWars.getHeadCache().getHead(member.getName());
            ItemMeta meta = stack.getItemMeta();
            Team team = Team.getTeam(member);
            if (team != null) {
                meta.setDisplayName("§8» " + team.getChatColor() + member.getName());
            }
            stack.setItemMeta(meta);
            inventory.setItem(slot++, stack);
        }

        player.playSound(player.getLocation(), Sound.ITEM_PICKUP, 1, 1);
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
            if (stack.getType() == Material.SKULL_ITEM && stack.getDurability() == 3) {
                SkullMeta meta = (SkullMeta) stack.getItemMeta();
                String owner = meta.getOwner();
                Player spectate = Bukkit.getPlayer(owner);
                if (spectate != null) {
                    player.closeInventory();
                    player.teleport(spectate.getLocation());
                    player.playSound(player.getLocation(), Sound.SUCCESSFUL_HIT, 1, 1);
                } else {
                    player.playSound(player.getLocation(), Sound.NOTE_BASS, 1, 1);
                }
            }
        }
    }

}
