package de.pauhull.paintwars;

import de.dytanic.cloudnet.bridge.CloudServer;
import de.pauhull.paintwars.command.SetLocationCommand;
import de.pauhull.paintwars.command.StartCommand;
import de.pauhull.paintwars.command.StatsCommand;
import de.pauhull.paintwars.data.MySQL;
import de.pauhull.paintwars.data.table.StatsTable;
import de.pauhull.paintwars.display.LobbyScoreboard;
import de.pauhull.paintwars.game.Team;
import de.pauhull.paintwars.inventory.SpectatorInventory;
import de.pauhull.paintwars.inventory.TeamInventory;
import de.pauhull.paintwars.listener.*;
import de.pauhull.paintwars.manager.ItemManager;
import de.pauhull.paintwars.manager.LocationManager;
import de.pauhull.paintwars.manager.TopPlayerManager;
import de.pauhull.paintwars.phase.GamePhaseHandler;
import de.pauhull.paintwars.util.CoinUtil;
import de.pauhull.paintwars.util.HeadCache;
import de.pauhull.paintwars.util.Selection;
import de.pauhull.paintwars.util.UUIDFetcher;
import de.pauhull.scoreboard.ScoreboardManager;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Paul
 * on 06.12.2018
 *
 * @author pauhull
 */
public class PaintWars extends JavaPlugin {

    @Getter
    private static PaintWars instance;

    @Getter
    private List<Player> spectators = new ArrayList<>();

    @Getter
    private HeadCache headCache;

    @Getter
    private GamePhaseHandler phaseHandler;

    @Getter
    private ItemManager itemManager;

    @Getter
    private LocationManager locationManager;

    @Getter
    private ScoreboardManager scoreboardManager;

    @Getter
    private TeamInventory teamInventory;

    @Getter
    private SpectatorInventory spectatorInventory;

    @Getter
    @Setter
    private Team winningTeam = null;

    @Getter
    private ScheduledExecutorService scheduledExecutorService;

    @Getter
    private ExecutorService executorService;

    @Getter
    private Selection gameArea = null;

    @Getter
    private YamlConfiguration config;

    @Getter
    private MySQL mySQL;

    @Getter
    private StatsTable statsTable;

    @Getter
    private Map<UUID, AtomicInteger> coloredBlocks;

    @Getter
    private List<UUID> winningPlayers = new ArrayList<>();

    @Getter
    private UUIDFetcher uuidFetcher;

    @Getter
    private TopPlayerManager topPlayerManager;

    @Getter
    private List<Block> woolBlocks;

    @Override
    public void onEnable() {
        instance = this;

        this.config = copyAndLoad("config.yml", new File(getDataFolder(), "config.yml"));
        Team.TEAM_SIZE = config.getInt("TeamSize");
        Team.MAX_PLAYERS = Team.TEAM_SIZE * Team.TEAM_AMOUNT;
        Team.MIN_PLAYERS = Team.TEAM_SIZE + 1;
        CoinUtil.COINS_AFTER_WIN = config.getDouble("CoinsAfterWin");
        CoinUtil.CREDITS_AFTER_WIN = config.getDouble("CreditsAfterWin");
        CoinUtil.COINS_AFTER_KILL = config.getDouble("CoinsAfterKill");
        CoinUtil.CREDITS_AFTER_KILL = config.getDouble("CreditsAfterKill");
        CoinUtil.COINS_FAIR_PLAY = config.getDouble("CoinsFairPlay");

        CloudServer.getInstance().setMaxPlayersAndUpdate(Team.MAX_PLAYERS);

        /* old code
        try {
            DedicatedPlayerList server = ((CraftServer) Bukkit.getServer()).getHandle();
            Field maxPlayers = server.getClass().getSuperclass().getDeclaredField("maxPlayers");
            maxPlayers.setAccessible(true);
            maxPlayers.set(server, Team.MAX_PLAYERS);
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
        }
         */

        this.coloredBlocks = new HashMap<>();
        this.locationManager = new LocationManager(this);
        this.scoreboardManager = new ScoreboardManager(this, LobbyScoreboard.class);
        this.phaseHandler = new GamePhaseHandler();
        this.uuidFetcher = new UUIDFetcher(this);
        this.itemManager = new ItemManager(this);
        this.teamInventory = new TeamInventory(this);
        this.spectatorInventory = new SpectatorInventory(this);
        this.headCache = new HeadCache();
        this.scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        this.executorService = Executors.newSingleThreadExecutor();
        this.mySQL = new MySQL(config.getString("MySQL.Host"),
                config.getString("MySQL.Port"),
                config.getString("MySQL.Database"),
                config.getString("MySQL.User"),
                config.getString("MySQL.Password"),
                config.getBoolean("MySQL.SSL"));

        if (!this.mySQL.connect()) {
            return;
        }

        this.statsTable = new StatsTable(mySQL, executorService);

        if (locationManager.isSet("pos1") && locationManager.isSet("pos2")) {
            gameArea = new Selection(locationManager.getLocation("pos1"),
                    locationManager.getLocation("pos2"));
        }
        this.woolBlocks = countWoolBlocks(gameArea);

        this.topPlayerManager = new TopPlayerManager(this);

        new SetLocationCommand(this);
        new StartCommand(this);
        new StatsCommand(this);

        new AsyncPlayerChatListener(this);
        new BlockBreakListener(this);
        new BlockPlaceListener(this);
        new EntityChangeBlockListener(this);
        new EntityDamageByEntityListener(this);
        new EntityDamageListener(this);
        new InventoryClickListener(this);
        new PlayerAchievementAwardedListener(this);
        new PlayerDeathListener(this);
        new PlayerDropItemListener(this);
        new PlayerInteractListener(this);
        new PlayerJoinListener(this);
        new PlayerLoginListener(this);
        new PlayerMoveListener(this);
        new PlayerPickupItemListener(this);
        new PlayerQuitListener(this);
        new ProjectileHitListener(this);
        new ProjectileLaunchListener(this);
        new ServerListPingListener(this);
        new WeatherChangeListener(this);
        new FoodLevelChangeListener(this);
    }

    @Override
    public void onDisable() {
        this.scheduledExecutorService.shutdown();
        this.executorService.shutdown();
    }

    private void copy(String resource, File file, boolean override) {
        if (!file.exists() || override) {
            file.getParentFile().mkdirs();

            if (file.exists()) {
                file.delete();
            }

            try {
                Files.copy(getResource(resource), file.toPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private YamlConfiguration copyAndLoad(String resource, File file) {
        copy(resource, file, false);
        return YamlConfiguration.loadConfiguration(file);
    }

    private List<Block> countWoolBlocks(Selection selection) {
        List<Block> blocks = new ArrayList<>();
        if (selection == null) return blocks;
        World world = selection.getA().getWorld();
        double minX = Math.min(selection.getA().getX(), selection.getB().getX());
        double minY = Math.min(selection.getA().getY(), selection.getB().getY());
        double minZ = Math.min(selection.getA().getZ(), selection.getB().getZ());
        double maxX = Math.max(selection.getA().getX(), selection.getB().getX());
        double maxY = Math.max(selection.getA().getY(), selection.getB().getY());
        double maxZ = Math.max(selection.getA().getZ(), selection.getB().getZ());

        for (double x = minX; x <= maxX; x++) {
            for (double y = minY; y <= maxY; y++) {
                for (double z = minZ; z <= maxZ; z++) {
                    Block block = new Location(world, x, y, z).getBlock();

                    if (block.getType() == Material.WOOL) {
                        blocks.add(block);
                    }
                }
            }
        }

        return blocks;
    }

    public static void broadcastMessage(final String message) {

        Bukkit.getOnlinePlayers().forEach(player -> player.sendMessage(message));
    }

}
