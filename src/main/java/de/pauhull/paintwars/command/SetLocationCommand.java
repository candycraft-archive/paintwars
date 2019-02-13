package de.pauhull.paintwars.command;

import de.pauhull.paintwars.Messages;
import de.pauhull.paintwars.PaintWars;
import de.pauhull.paintwars.Permissions;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Created by Paul
 * on 07.12.2018
 *
 * @author pauhull
 */
public class SetLocationCommand implements CommandExecutor {

    private PaintWars paintWars;

    public SetLocationCommand(PaintWars paintWars) {
        this.paintWars = paintWars;

        paintWars.getCommand("setlocation").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission(Permissions.SETLOCATION)) {
            sender.sendMessage(Messages.PREFIX + Messages.NO_PERMISSIONS);
            return true;
        }

        if (!(sender instanceof Player)) {
            sender.sendMessage(Messages.PREFIX + Messages.ONLY_PLAYERS);
            return true;
        }
        Player player = (Player) sender;

        if (args.length == 0) {
            sender.sendMessage(Messages.PREFIX + "§c/setlocation <Location>");
            return true;
        }

        paintWars.getLocationManager().saveLocation(player.getLocation(), args[0]);
        sender.sendMessage(Messages.PREFIX + "Du hast deine Location unter §e" + args[0] + "§7 gespeichert!");

        return true;
    }

}
