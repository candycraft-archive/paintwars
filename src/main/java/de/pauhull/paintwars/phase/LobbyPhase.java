package de.pauhull.paintwars.phase;

import de.pauhull.paintwars.Messages;
import de.pauhull.paintwars.PaintWars;
import de.pauhull.paintwars.game.Team;
import de.pauhull.paintwars.util.ActionBar;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

/**
 * Created by Paul
 * on 07.12.2018
 *
 * @author pauhull
 */
public class LobbyPhase extends GamePhase implements Runnable {

    @Getter
    private Type type = Type.LOBBY;

    private int countdown;
    private int second;

    public LobbyPhase(GamePhaseHandler handler) {
        super(handler);
    }

    private static String _p() {
        return "";
    }

    @Override
    public void start() {
        super.start();
        countdown = -1;
    }

    @Override
    public void run() { // gets run every second

        int onlineCount = Bukkit.getOnlinePlayers().size();

        if (onlineCount >= Team.MIN_PLAYERS && countdown == -1) {
            Bukkit.broadcastMessage(Messages.PREFIX + "§7Der Countdown wurde §agestartet!");
            countdown = 60;
            return;
        }

        if (onlineCount < Team.MIN_PLAYERS && countdown != -1) {
            Bukkit.broadcastMessage(Messages.PREFIX + "Der Countdown wurde §cabgebrochen§7, da nicht genügend Spieler online sind.");
            for (Player player : Bukkit.getOnlinePlayers()) {
                player.setExp(0);
                player.setLevel(0);
            }
            countdown = -1;
            return;
        }

        if (countdown == -1) {
            String message;

            int missingPlayers = Team.MIN_PLAYERS - Bukkit.getOnlinePlayers().size();
            if (missingPlayers != 1) {
                message = "Es fehlen noch §c" + (Team.MIN_PLAYERS - Bukkit.getOnlinePlayers().size()) + "§f Spieler.";
            } else {
                message = "Es fehlt noch §cein§f Spieler.";
            }

            for (Player player : Bukkit.getOnlinePlayers()) {
                ActionBar.sendActionBar(player, message);
            }
            return;
        }

        for (Player player : Bukkit.getOnlinePlayers()) {
            player.setExp((float) countdown / 60f);
            player.setLevel(countdown);
        }

        if (countdown == 60 || countdown == 50 || countdown == 40 || countdown == 30 || countdown == 20
                || countdown == 15 || countdown == 10 || countdown == 9 || countdown == 8 || countdown == 7
                || countdown == 6 || countdown == 5 || countdown == 4 || countdown == 3 || countdown == 2 || countdown == 1) {

            for (Player player : Bukkit.getOnlinePlayers()) {
                if (countdown == 1) {
                    player.playSound(player.getLocation(), Sound.NOTE_BASS, 1, 2);
                    ActionBar.sendActionBar(player, "Noch §eeine §fSekunde");
                } else {
                    player.playSound(player.getLocation(), Sound.NOTE_BASS, 1, 1);
                    ActionBar.sendActionBar(player, "Noch §e" + countdown + " §fSekunden");
                }
            }
        }

        if (countdown == 0) {
            this.end();
        }

        countdown--;
    }

    @Override
    public void end() {
        super.end();
        Bukkit.broadcastMessage(Messages.PREFIX + "Das Spiel §astartet§7!");
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (Team.getTeam(player) == null) {
                Team team = Team.findFreeTeam();

                if (team == null) {
                    player.kickPlayer(Messages.PREFIX + "§cKein §7freies Team gefunden.");
                } else {
                    player.sendMessage(Messages.PREFIX + "Du wurdest dem Team " + team.getColoredName() + "§7 zugewiesen!");
                    team.getMembers().add(player);
                    team.giveArmor(player);
                    PaintWars.getInstance().getScoreboardManager().updateTeam(player);
                }
            }
        }
        handler.startPhase(IngamePhase.class);
    }

}
