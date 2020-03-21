package de.pauhull.paintwars.listener;

import de.pauhull.paintwars.Messages;
import de.pauhull.paintwars.PaintWars;
import de.pauhull.paintwars.game.Disguises;
import de.pauhull.paintwars.game.Team;
import de.pauhull.paintwars.phase.GamePhase;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * Created by Paul
 * on 07.12.2018
 *
 * @author pauhull
 */
public class PlayerQuitListener extends ListenerTemplate {

    public PlayerQuitListener(PaintWars paintWars) {
        super(paintWars);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        Disguises.undisguise(player);

        if (paintWars.getPhaseHandler().getActivePhaseType() == GamePhase.Type.INGAME) {
            int activeTeams = 0;

            teamLoop:
            for (Team team : Team.values()) {
                for (Player member : team.getMembers()) {
                    if (member.getName().equals(player.getName())) {
                        continue;
                    }

                    activeTeams++;
                    continue teamLoop;
                }
            }
            if (activeTeams <= 1) {
                paintWars.getPhaseHandler().getActivePhase().end();
            }
        }

        paintWars.getSpectators().remove(player);

        Team team = Team.getTeam(player);
        if (team != null) {
            team.getMembers().remove(player);
        }

        if (paintWars.getPhaseHandler().getActivePhaseType() == GamePhase.Type.LOBBY) {
            if (PlayerMoveListener.getInstance().getLobbyBlocks().containsKey(player)) {
                for (Block block : PlayerMoveListener.getInstance().getLobbyBlocks().get(player)) {
                    block.setType(Material.BARRIER);
                    block.setData((byte) 0);
                }
                PlayerMoveListener.getInstance().getLobbyBlocks().remove(player);
            }

            event.setQuitMessage(Messages.PREFIX + "§e" + player.getName() + "§7 hat das Spiel §cverlassen§7! §8[§e"
                    + (Bukkit.getOnlinePlayers().size() - 1) + "§8/§e" + Team.MAX_PLAYERS + "§8]");
        } else {
            Team currentTeam = Team.getTeam(player);
            if (currentTeam != null) {
                event.setQuitMessage(Messages.PREFIX + currentTeam.getChatColor() + player.getName() + "§7 hat das Spiel §cverlassen!");
            } else {
                event.setQuitMessage(null);
            }
        }
    }

}
