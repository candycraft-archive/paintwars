package de.pauhull.paintwars.command;

import de.pauhull.paintwars.Messages;
import de.pauhull.paintwars.PaintWars;
import de.pauhull.paintwars.Permissions;
import de.pauhull.paintwars.game.Team;
import de.pauhull.paintwars.phase.GamePhase;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

/**
 * Created by Paul
 * on 07.12.2018
 *
 * @author pauhull
 */
public class StartCommand implements CommandExecutor {

    //TODO item

    private PaintWars paintWars;

    public StartCommand(PaintWars paintWars) {
        this.paintWars = paintWars;

        paintWars.getCommand("start").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission(Permissions.START)) {
            sender.sendMessage(Messages.PREFIX + "Nur §ePremium+ §7Spieler dürfen diesen Befehl benutzen!");
            return true;
        }

        if (paintWars.getPhaseHandler().getActivePhaseType() != GamePhase.Type.LOBBY) {
            sender.sendMessage(Messages.PREFIX + "Das Spiel ist §cbereits §7gestartet!");
            return true;
        }

        if (Bukkit.getOnlinePlayers().size() < Team.MIN_PLAYERS) {
            sender.sendMessage(Messages.PREFIX + "Es sind §cnicht §7genug Spieler vorhanden, um das Spiel zu starten!");
            return true;
        }

        paintWars.getPhaseHandler().getActivePhase().end();
        return true;
    }


}
