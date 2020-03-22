package de.pauhull.paintwars.display;

import de.dytanic.cloudnet.bridge.CloudServer;
import de.pauhull.paintwars.game.Team;
import de.pauhull.scoreboard.CustomScoreboard;
import net.mcstats2.bridge.server.bukkit.MCPerms;
import net.mcstats2.permissions.manager.data.MCSGroupData;
import org.bukkit.Bukkit;
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
        new DisplayScore(" §6§lStyleMC§7.§6de");
        new DisplayScore("§fServer:");
        new DisplayScore();
        new DisplayScore(" §c" + CloudServer.getInstance().getMotd());
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

        String prefix;
        String rank;
        if (player.getDisplayName().equals(player.getName())) {
            MCSGroupData group = MCPerms.getInstance().getManager().getHighestGroup(player.getUniqueId());
            rank = group.tagID + "";
            prefix = group.prefix;
        } else {
            rank = "65";
            prefix = "§a";
        }

        Team team = Team.getTeam(player);

        String name = team != null ? team.name() + rank + player.getName() : "Z" + rank + player.getName();
        if (name.length() > 16) {
            name = name.substring(0, 16);
        }

        if (scoreboard.getTeam(name) != null) {
            scoreboard.getTeam(name).unregister();
        }

        org.bukkit.scoreboard.Team scoreboardTeam = scoreboard.registerNewTeam(name);

        String suffix = "";
        String suffixColor = "";
        if (team != null) {
            suffix = " [" + team.getName() + "]";
            suffixColor = team.getChatColor().toString();
        }

        scoreboardTeam.setSuffix(suffixColor + suffix);
        scoreboardTeam.setPrefix(prefix);
        scoreboardTeam.addEntry(player.getName());
    }

}
