package de.pauhull.paintwars.game;

import de.pauhull.paintwars.PaintWars;
import de.pauhull.paintwars.phase.GamePhase;
import lombok.Getter;
import org.bukkit.entity.Player;

import java.util.*;

/**
 * Created by Paul
 * on 09.12.2018
 *
 * @author pauhull
 */
public class Disguises {

    @Getter
    private static Map<Player, Team> disguises = new HashMap<>();

    public static Team disguise(Player player) {
        Team team = findTeamToDisguise(player);
        if (team != null) {
            disguises.put(player, team);
            team.giveArmor(player);
            PaintWars.getInstance().getScoreboardManager().updateTeam(player);
        }
        return team;
    }

    public static void undisguise(Player player) {
        if (disguises.containsKey(player)) {
            disguises.remove(player);
            PaintWars.getInstance().getScoreboardManager().updateTeam(player);
            Team team = Team.getTeam(player);
            if (team != null) {
                team.giveArmor(player);
            }
        }
    }

    public static boolean isDisguised(Player player) {
        return disguises.containsKey(player);
    }

    public static Team findTeamToDisguise(Player player) {
        Team currentTeam = Team.getTeam(player);

        if (PaintWars.getInstance().getPhaseHandler().getActivePhaseType() != GamePhase.Type.INGAME || currentTeam == null) {
            return null;
        }

        List<Team> shuffledList = Arrays.asList(Team.values());
        Collections.shuffle(shuffledList);
        Team[] teams = shuffledList.toArray(new Team[0]);

        for (Team team : teams) {
            if (team != currentTeam && !team.getMembers().isEmpty()) {
                return team;
            }
        }

        return null;
    }

}
