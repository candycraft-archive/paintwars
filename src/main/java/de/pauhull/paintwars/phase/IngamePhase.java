package de.pauhull.paintwars.phase;

import cloud.timo.TimoCloud.api.TimoCloudAPI;
import de.godtitan.coins.CoinAPI;
import de.pauhull.paintwars.Messages;
import de.pauhull.paintwars.PaintWars;
import de.pauhull.paintwars.display.IngameScoreboard;
import de.pauhull.paintwars.game.Powerup;
import de.pauhull.paintwars.game.Team;
import de.pauhull.paintwars.util.ActionBar;
import de.pauhull.paintwars.util.Selection;
import de.pauhull.paintwars.util.Title;
import de.pauhull.utils.misc.RandomFireworkGenerator;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Created by Paul
 * on 07.12.2018
 *
 * @author pauhull
 */
public class IngamePhase extends GamePhase {

    private static Random random = new Random();

    @Getter
    private static Map<String, Double> percentages = new HashMap<>();

    @Getter
    private Type type = Type.INGAME;

    @Getter
    private int time = 0;

    private int nextPowerupTime;

    public IngamePhase(GamePhaseHandler handler) {
        super(handler);
        this.nextPowerupTime = chooseNextPowerupTime();
    }

    @Override
    public void run() {
        time++;

        if (time >= TimeUnit.MINUTES.toSeconds(5)) {
            this.end();
            return;
        }

        if (time == nextPowerupTime) {
            Powerup.spawnPowerup();
            nextPowerupTime = chooseNextPowerupTime();
        }

        timeCheck:
        if (time % 60 == 0) {
            int minutes = 5 - time / 60;
            if (minutes == 1) {
                Bukkit.broadcastMessage(Messages.PREFIX + "Noch §eeine §7Minute!");
            } else if (minutes > 1) {
                Bukkit.broadcastMessage(Messages.PREFIX + "Noch §e" + minutes + "§7 Minuten!");
            } else {
                break timeCheck;
            }

            for (Player player : Bukkit.getOnlinePlayers()) {
                player.playSound(player.getLocation(), Sound.NOTE_BASS, 1, 1);
            }
        }

        /*if(time % 1 == 0) {*/
        countBlocks();
        /*}*/

        for (Team team : Team.values()) {
            for (Player player : team.getMembers()) {
                ActionBar.sendActionBar(player, "§fTeam " + team.getColoredName() + "§f: §b" + ((int) (percentages.get(team.name()) * 10000.0) / 100.0D) + "%");
            }
        }
    }

    @Override
    public void start() {

        TimoCloudAPI.getBukkitAPI().getThisServer().setState("INGAME");

        for (Team team : Team.values()) {
            for (Player player : team.getMembers()) {
                PaintWars.getInstance().getLocationManager().teleport(player, team.name());
                player.playSound(player.getLocation(), Sound.LEVEL_UP, 1, 1);
                PaintWars.getInstance().getItemManager().giveIngameItems(player);
                Title.sendTitle(player, "§8× §e§lPaint§6§lWars§8 ×", "§7Das Spiel §abeginnt§7!", 0, 40, 20);
            }
        }

        PaintWars.getInstance().getScoreboardManager().setScoreboard(IngameScoreboard.class);
        countBlocks();
        super.start();
    }

    @Override
    public void end() {
        super.end();
        this.countBlocks();

        Team team = Team.RED;
        double percentage = Double.NEGATIVE_INFINITY;
        for (Team check : Team.values()) {
            if (percentages.get(check.name()) > percentage) {
                team = check;
                percentage = percentages.get(check.name());
            }
        }

        for (Player player : team.getMembers()) {
            RandomFireworkGenerator.shootRandomFirework(player.getLocation(), 10);
        }

        for (Player player : Bukkit.getOnlinePlayers()) {
            Title.sendTitle(player, "§7Team " + team.getColoredName() + " §7hat das Spiel §agewonnen§7!",
                    team.getMembers().contains(player) ? "§a+§775 Coins" : "", 0, 40, 20);
            if (team.getMembers().contains(player)) {
                CoinAPI.getInstance().addCoins(player.getUniqueId(), 75);
                PaintWars.getInstance().getWinningPlayers().add(player.getUniqueId());
            }

            if (PaintWars.getInstance().getColoredBlocks().containsKey(player.getUniqueId())) {
                PaintWars.getInstance().getStatsTable().getStats(player.getUniqueId(), stats -> {
                    if (stats == null) return;
                    stats.setColoredBlocks(stats.getColoredBlocks() + PaintWars.getInstance().getColoredBlocks().get(player.getUniqueId()).get());
                    PaintWars.getInstance().getStatsTable().setStats(player.getUniqueId(), stats);
                    PaintWars.getInstance().getColoredBlocks().remove(player.getUniqueId());
                });
            }
        }

        Bukkit.broadcastMessage("§8§m                                 ");
        Bukkit.broadcastMessage("§7Team " + team.getColoredName() + "§7 hat das Spiel §agewonnen§7!");
        Bukkit.broadcastMessage(" ");

        // sort map
        Comparator<Map.Entry<String, Double>> comparator = (e1, e2) -> {
            double d1 = e1.getValue();
            double d2 = e2.getValue();
            return Double.compare(d1, d2) * -1; // invert so that bigger doubles are at the top
        };
        Map<String, Double> sortedMap = percentages.entrySet().stream().sorted(comparator).
                collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));

        for (String teamName : sortedMap.keySet()) {
            String amount = (int) ((sortedMap.get(teamName) * 10000.0) / 100.0) + "%";
            if (teamName.equals("UNCOLORED")) {
                Bukkit.broadcastMessage("§8× §7Ungefärbt: §b" + amount);
                continue;
            }

            Team checkTeam = Team.valueOf(teamName);
            Bukkit.broadcastMessage("§8× " + checkTeam.getColoredName() + "§7: §b" + amount);
        }
        Bukkit.broadcastMessage("§8§m                                 ");

        PaintWars.getInstance().getScoreboardManager().updateTitle("§e§lPaint§6§lWars §8| §7Ende");

        handler.startPhase(EndPhase.class).setStartTime(startTime);
    }

    private void countBlocks() {
        Selection selection = PaintWars.getInstance().getGameArea();

        if (selection != null) {
            List<Block> blocks = selection.getBlocks();
            int uncolored = Selection.countBlocks(blocks, Material.WOOL, (short) 0);
            int red = Selection.countBlocks(blocks, Material.WOOL, Team.RED.getDyeColor().getWoolData());
            int green = Selection.countBlocks(blocks, Material.WOOL, Team.GREEN.getDyeColor().getWoolData());
            int blue = Selection.countBlocks(blocks, Material.WOOL, Team.BLUE.getDyeColor().getWoolData());
            int yellow = Selection.countBlocks(blocks, Material.WOOL, Team.YELLOW.getDyeColor().getWoolData());
            int total = uncolored + red + green + blue + yellow;

            percentages.put("UNCOLORED", uncolored / (double) total);
            percentages.put(Team.RED.name(), red / (double) total);
            percentages.put(Team.GREEN.name(), green / (double) total);
            percentages.put(Team.BLUE.name(), blue / (double) total);
            percentages.put(Team.YELLOW.name(), yellow / (double) total);
        }
    }

    private int chooseNextPowerupTime() {
        return time + random.nextInt(30) + 45;
    }

}
