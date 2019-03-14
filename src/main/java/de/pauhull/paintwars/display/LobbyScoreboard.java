package de.pauhull.paintwars.display;

import cloud.timo.TimoCloud.api.TimoCloudAPI;
import de.pauhull.friends.common.party.Party;
import de.pauhull.friends.spigot.SpigotFriends;
import de.pauhull.paintwars.PaintWars;
import de.pauhull.paintwars.game.Team;
import de.pauhull.scoreboard.CustomScoreboard;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;


/**
 * Created by Paul
 * on 07.12.2018
 *
 * @author pauhull
 */
public class LobbyScoreboard extends CustomScoreboard {

    private DisplayScore online, team;

    public LobbyScoreboard(Player player) {
        super(player, player.getName() + "_lobby", "§5§lPaintWars Lobby");
        this.descending = false;
    }

    @Override
    public void show() {
        new DisplayScore(" §d§lCandyCraft§7.§dde");
        new DisplayScore("§fServer:");
        new DisplayScore();
        new DisplayScore(" §c" + TimoCloudAPI.getBukkitAPI().getThisServer().getMap());
        new DisplayScore("§fMap:");
        new DisplayScore();
        this.team = new DisplayScore(" §7Lädt...");
        new DisplayScore("§fTeam:");
        new DisplayScore();
        this.online = new DisplayScore(" §aLädt...");
        new DisplayScore("§fOnline:");
        new DisplayScore();

        super.show();
    }

    @Override
    public void update() {
        String online = " §a" + Bukkit.getOnlinePlayers().size();
        if (!this.online.getScore().getEntry().equals(online)) {
            this.online.setName(online);
        }

        Team team = Team.getTeam(player);
        String teamName = "§bZufällig";
        if (team != null) {
            teamName = team.getColoredName();
        }
        String teamScore = " " + teamName;
        if (!this.team.getScore().getEntry().equals(teamScore)) {
            this.team.setName(teamScore);
        }
    }

    @Override
    public void updateTeam(Player player) {
        SpigotFriends.getInstance().getPartyManager().getAllParties(parties -> {
            Bukkit.getScheduler().runTask(PaintWars.getInstance(), () -> {

                Team team = Team.getTeam(player);

                String name = team != null ? team.name() + player.getName() : "Z" + player.getName();
                if (name.length() > 16) {
                    name = name.substring(0, 16);
                }

                if (scoreboard.getTeam(name) != null) {
                    scoreboard.getTeam(name).unregister();
                }

                org.bukkit.scoreboard.Team scoreboardTeam = scoreboard.registerNewTeam(name);

                StringBuilder suffix = new StringBuilder();
                for (Party party : parties) {
                    if (party.getMembers().contains(player.getDisplayName()) && party.getMembers().contains(this.player.getDisplayName())) {
                        suffix.append("§7 [§5Party§7]");
                    }
                }
                scoreboardTeam.setSuffix(suffix.toString());

                if (team == null) {
                    scoreboardTeam.setPrefix(ChatColor.GRAY.toString());
                } else {
                    scoreboardTeam.setPrefix(team.getChatColor().toString());
                }

                scoreboardTeam.addEntry(player.getName());
            });
        });
    }

}
