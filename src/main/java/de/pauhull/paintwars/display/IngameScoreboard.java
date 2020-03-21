package de.pauhull.paintwars.display;

import de.pauhull.paintwars.PaintWars;
import de.pauhull.paintwars.game.Disguises;
import de.pauhull.paintwars.game.Team;
import de.pauhull.paintwars.phase.GamePhase;
import de.pauhull.paintwars.phase.IngamePhase;
import de.pauhull.scoreboard.CustomScoreboard;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Paul
 * on 07.12.2018
 *
 * @author pauhull
 */
public class IngameScoreboard extends CustomScoreboard {

    private DisplayScore uncolored, red = null, green = null, blue = null, yellow = null;

    public IngameScoreboard(Player player) {
        super(player, player.getName() + "_ingame", "§e§lPaint§6§lWars §8| §cLädt...");
    }

    @Override
    public void show() {

        new DisplayScore().setScore(101);

        this.uncolored = new DisplayScore(" Ungefärbt: §cLädt...");

        int id = 5;
        if (!Team.RED.getMembers().isEmpty())
            this.red = new DisplayScore(" " + Team.RED.getColoredName() + ": §cLädt...", id--);
        if (!Team.GREEN.getMembers().isEmpty())
            this.green = new DisplayScore(" " + Team.GREEN.getColoredName() + ": §cLädt...", id--);
        if (!Team.BLUE.getMembers().isEmpty())
            this.blue = new DisplayScore(" " + Team.BLUE.getColoredName() + ": §cLädt...", id--);
        if (!Team.YELLOW.getMembers().isEmpty())
            this.yellow = new DisplayScore(" " + Team.YELLOW.getColoredName() + ": §cLädt...", id);

        this.nextScoreID = -1;
        new DisplayScore();
        new DisplayScore("Server:");
        new DisplayScore(" §6§lStyleMC§7.§6de");

        super.show();
    }

    @Override
    public void update() {

        // update title
        if (PaintWars.getInstance().getPhaseHandler().getActivePhaseType() == GamePhase.Type.INGAME) {
            long time = 0;
            if (PaintWars.getInstance().getPhaseHandler().getActivePhaseType() == GamePhase.Type.INGAME) {
                time = 60 * 5 * 1000 - ((IngamePhase) PaintWars.getInstance().getPhaseHandler().getActivePhase()).getTime() * 1000;
            }
            String title = "§e§lPaint§6§lWars §8| §e" + new SimpleDateFormat("mm:ss").format(new Date(time));
            if (!objective.getDisplayName().equals(title)) {
                this.updateTitle(title);
            }
        }

        // update percentages
        if (IngamePhase.getPercentages().containsKey("UNCOLORED")) {
            double percentage = IngamePhase.getPercentages().get("UNCOLORED");
            String uncolored = " Ungefärbt: §b" + ((int) (percentage * 10000.0) / 100.0D) + "%";
            if (!this.uncolored.getScore().getEntry().equals(uncolored)) {
                this.uncolored.setName(uncolored);
                this.uncolored.setScore((int) (percentage * 100));
            }
        }
        for (Team team : Team.values()) {
            if (IngamePhase.getPercentages().containsKey(team.name())) {
                double percentage = IngamePhase.getPercentages().get(team.name());
                DisplayScore score = null;
                switch (team) {
                    case RED:
                        score = red;
                        break;
                    case GREEN:
                        score = green;
                        break;
                    case BLUE:
                        score = blue;
                        break;
                    case YELLOW:
                        score = yellow;
                        break;
                }

                if (score != null) {
                    String newScore = " " + team.getColoredName() + ": §b" + ((int) (percentage * 10000.0) / 100.0D) + "%";
                    if (!score.getScore().getEntry().equals(newScore)) {
                        score.setName(newScore);
                        score.setScore((int) (percentage * 100));
                    }
                }
            }
        }
    }

    @Override
    public void updateTeam(Player player) {

        Team team = Team.getTeam(player);
        Team fakeTeam = Disguises.getDisguises().get(player);

        if (fakeTeam != null && Team.getTeam(this.player) != team) {
            team = fakeTeam;
        }

        String name = team != null ? team.name() + player.getName() : ("Z" + player.getName());
        if (name.length() > 16) {
            name = name.substring(0, 16);
        }

        if (scoreboard.getTeam(name) != null) {
            scoreboard.getTeam(name).unregister();
        }

        org.bukkit.scoreboard.Team scoreboardTeam = scoreboard.registerNewTeam(name);

        if (team == null) {
            scoreboardTeam.setPrefix(ChatColor.DARK_GRAY.toString());
        } else {
            scoreboardTeam.setPrefix(team.getChatColor().toString());
        }

        scoreboardTeam.addEntry(player.getName());
    }

}
