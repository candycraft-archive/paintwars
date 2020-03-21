package de.pauhull.paintwars.phase;

import de.pauhull.paintwars.PaintWars;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

/**
 * Created by Paul
 * on 08.12.2018
 *
 * @author pauhull
 */
public class EndPhase extends GamePhase {

    @Getter
    private Type type = Type.ENDING;

    private int seconds = 0;

    public EndPhase(GamePhaseHandler handler) {
        super(handler);
    }

    @Override
    public void run() {
        if (seconds >= 10) {
            this.end();
        }

        if (seconds++ == 3) {
            for (UUID uuid : PaintWars.getInstance().getWinningPlayers()) {
                PaintWars.getInstance().getStatsTable().getStats(uuid, stats -> {
                    if (stats == null) return;
                    stats.setWins(stats.getWins() + 1);
                    PaintWars.getInstance().getStatsTable().setStats(uuid, stats);
                });
            }
        }
    }

    @Override
    public void end() {
        super.end();
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.kickPlayer("");
        }
        Bukkit.getServer().shutdown();
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

}
