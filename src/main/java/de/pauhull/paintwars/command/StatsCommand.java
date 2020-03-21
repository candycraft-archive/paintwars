package de.pauhull.paintwars.command;

import de.pauhull.paintwars.Messages;
import de.pauhull.paintwars.PaintWars;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.UUID;

/**
 * Created by Paul
 * on 07.12.2018
 *
 * @author pauhull
 */
public class StatsCommand implements CommandExecutor {

    private PaintWars paintWars;

    public StatsCommand(PaintWars paintWars) {
        this.paintWars = paintWars;

        paintWars.getCommand("stats").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (args.length < 1) {
            if (sender instanceof Player) {
                sendStats(sender, ((Player) sender).getUniqueId(), null);
            } else {
                sender.sendMessage(Messages.PREFIX + Messages.ONLY_PLAYERS);
            }
        } else {
            paintWars.getUuidFetcher().fetchProfile(args[0], (uuid, name) -> {
                sendStats(sender, uuid, name);
            });
        }

        return true;
    }

    private void sendStats(CommandSender sender, UUID uuid, String playerName) {
        paintWars.getStatsTable().getStats(uuid, stats -> {
            paintWars.getStatsTable().getUserRanking(uuid, ranking -> {
                NumberFormat format = NumberFormat.getInstance(Locale.GERMAN);

                if (playerName == null) {
                    sender.sendMessage("§8§m---|§e Stats §8§m|---");
                } else {
                    sender.sendMessage("§8§m---|§e Stats von §6" + playerName + " §8§m|---");
                }

                sender.sendMessage(" ");

                if (ranking != -1) {
                    sender.sendMessage("§e§lRanking§8 » §7" + format.format(ranking));
                } else {
                    sender.sendMessage("§e§lRanking§8 » §7Unbekannt");
                }
                sender.sendMessage("§eSiege §8» §7" + format.format(stats.getWins()));
                sender.sendMessage("§eKills §8» §7" + format.format(stats.getKills()));
                sender.sendMessage("§eTode §8» §7" + format.format(stats.getDeaths()));
                sender.sendMessage("§eK/D §8» §7" + format(stats.getKD()));
                sender.sendMessage("§eAufgesammelte Powerups §8» §7" + format.format(stats.getCollectedPowerups()));

                int coloredBlocks = 0;
                if (paintWars.getColoredBlocks().containsKey(uuid)) {
                    coloredBlocks = paintWars.getColoredBlocks().get(uuid).get();
                }

                sender.sendMessage("§eGefärbte Wollblöcke §8» §7" + format.format(stats.getColoredBlocks() + coloredBlocks));
                sender.sendMessage(" ");

                if (playerName == null) {
                    sender.sendMessage("§8§m---|§e Stats §8§m|---");
                } else {
                    sender.sendMessage("§8§m---|§e Stats von §6" + playerName + " §8§m|---");
                }
            });
        });
    }

    private String format(double kd) {
        if (kd == Double.POSITIVE_INFINITY || kd == 0) {
            return "NaN";
        } else {
            return new DecimalFormat("#0.00").format(kd);
        }
    }

}
