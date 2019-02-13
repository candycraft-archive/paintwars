package de.pauhull.paintwars.game;

import de.pauhull.paintwars.util.ItemBuilder;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by Paul
 * on 07.12.2018
 *
 * @author pauhull
 */
public enum Team {

    BLUE(ChatColor.BLUE, DyeColor.BLUE, "Blau", (short) 11),
    GREEN(ChatColor.GREEN, DyeColor.LIME, "Grün", (short) 13),
    RED(ChatColor.RED, DyeColor.RED, "Rot", (short) 14),
    YELLOW(ChatColor.YELLOW, DyeColor.YELLOW, "Gelb", (short) 4);

    public static int TEAM_SIZE = 2;
    public static int TEAM_AMOUNT = Team.values().length;
    public static int MAX_PLAYERS = TEAM_SIZE * TEAM_AMOUNT;
    public static int MIN_PLAYERS = TEAM_SIZE + 1;

    @Getter
    private ChatColor chatColor;

    @Getter
    private DyeColor dyeColor;

    @Getter
    private String name;

    @Getter
    private List<Player> members;

    @Getter
    private short glassPaneColor;

    Team(ChatColor chatColor, DyeColor dyeColor, String name, short glassPaneColor) {
        this.chatColor = chatColor;
        this.dyeColor = dyeColor;
        this.name = name;
        this.glassPaneColor = glassPaneColor;
        this.members = new ArrayList<>();
    }

    public static Team findFreeTeam() {
        int i = 0;

        List<Team> shuffledList = Arrays.asList(values());
        Collections.shuffle(shuffledList);
        Team[] teams = shuffledList.toArray(new Team[0]);
        Team team;
        do {
            if (i >= teams.length) {
                return null;
            }

            team = teams[i++];
        } while (team.getMembers().size() >= TEAM_SIZE);
        return team;
    }

    public static Team getTeam(Player player) {
        for (Team team : values()) {
            if (team.getMembers().contains(player)) {
                return team;
            }
        }

        return null;
    }

    public String getColoredName() {
        return chatColor + name;
    }

    public void giveArmor(Player player) {
        ItemStack helmet = new ItemBuilder(Material.LEATHER_HELMET).setColor(dyeColor.getColor()).setDisplayName("§8⬛ " + chatColor + "Helm §8«").setUnbreakable(true).build();
        ItemStack chestplate = new ItemBuilder(Material.LEATHER_CHESTPLATE).setColor(dyeColor.getColor()).setDisplayName("§8⬛ " + chatColor + "Brustplatte §8«").setUnbreakable(true).build();
        ItemStack leggings = new ItemBuilder(Material.LEATHER_LEGGINGS).setColor(dyeColor.getColor()).setDisplayName("§8⬛ " + chatColor + "Röhrenjeans §8«").setUnbreakable(true).build();
        ItemStack boots = new ItemBuilder(Material.LEATHER_BOOTS).setColor(dyeColor.getColor()).setDisplayName("§8⬛ " + chatColor + "Schuhe §8«").setUnbreakable(true).build();
        player.getInventory().setArmorContents(new ItemStack[]{boots, leggings, chestplate, helmet});
    }

}
