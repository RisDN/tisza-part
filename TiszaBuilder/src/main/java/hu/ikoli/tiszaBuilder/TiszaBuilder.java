package hu.ikoli.tiszabuilder;

import java.io.File;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import hu.ikoli.tiszabuilder.building.Building;
import hu.ikoli.tiszabuilder.building.BuildingPlayer;
import hu.ikoli.tiszabuilder.building.PlayerStats;
import hu.ikoli.tiszabuilder.config.Config;
import hu.ikoli.tiszabuilder.config.ServerType;
import hu.ikoli.tiszabuilder.listeners.CommandListener;
import hu.ikoli.tiszabuilder.listeners.InventoryCloseListener;
import hu.ikoli.tiszabuilder.listeners.PlayerJoinListener;
import hu.ikoli.tiszabuilder.listeners.PlayerQuitListener;
import hu.ikoli.tiszabuilder.redis.JedisConnection;
import hu.ikoli.tiszabuilder.utils.FileManager;
import me.pikamug.localelib.LocaleLib;
import me.pikamug.localelib.LocaleManager;

public class TiszaBuilder extends JavaPlugin {

	private static TiszaBuilder instance;
	private static Config config;
	private static PlaceholderManager placeholderManager;
	private static File pluginDataFolder;
	private static FileManager playerData;
	private static JedisConnection jedisConnection;
	private static LocaleManager localeManager;

	private BukkitTask playerSavingTask;
	private BukkitTask redisFetchTask;
	private BukkitTask redisSyncTask;

	public void onEnable() {
		instance = this;

		CommandListener commandListener = new CommandListener();
		getCommand("builder").setExecutor(commandListener);
		getServer().getPluginManager().registerEvents(commandListener, instance);

		getServer().getPluginManager().registerEvents(new InventoryCloseListener(), this);

		getServer().getPluginManager().registerEvents(new PlayerJoinListener(), this);
		getServer().getPluginManager().registerEvents(new PlayerQuitListener(), this);

		LocaleLib localeLib = (LocaleLib) getServer().getPluginManager().getPlugin("LocaleLib");
		if (localeLib != null) {
			localeManager = localeLib.getLocaleManager();
		}

		pluginDataFolder = getDataFolder();
		config = new Config();
		placeholderManager = new PlaceholderManager();
		playerData = new FileManager("playerdata.yml");
		jedisConnection = new JedisConnection();

		File buildingFolder = new File(getDataFolder(), "buildings");
		if (!buildingFolder.exists()) {
			buildingFolder.mkdirs();
			return;
		}

		if (Config.getServerType().equals(ServerType.BUILDING)) {
			for (File file : buildingFolder.listFiles()) {
				if (file.getName().endsWith(".yml")) {
					new Building(file.getName().replace(".yml", ""));
				}
			}
		}

		for (Player player : getServer().getOnlinePlayers()) {
			BuildingPlayer.getBuildingPlayer(player);
		}

		startPlayerSavingTask();

		startRedisSyncTask();

		getLogger().info("Builder plugin has been enabled!");
		getLogger().info("Server type: " + Config.getServerType().name());
	}

	public void startRedisSyncTask() {
		getLogger().info("Starting redis sync task...");
		int syncInterval = Config.getInt("settings.redis.sync-interval");

		if (Config.getServerType().equals(ServerType.GATHERING)) {
			redisFetchTask = getServer().getScheduler().runTaskTimerAsynchronously(this, () -> {

				PlayerStats.fetch();

			}, syncInterval * 20, syncInterval * 20);
		}

		if (Config.getServerType().equals(ServerType.BUILDING)) {

			redisSyncTask = getServer().getScheduler().runTaskTimerAsynchronously(this, () -> {
				Building building = Building.getBuildings().get(0);
				building.syncStatsToRedis();
			}, syncInterval * 20, syncInterval * 20);

		}

	}

	public void stopRedisSyncTask() {

		if (redisFetchTask != null) {
			redisFetchTask.cancel();
			return;
		}

		if (redisSyncTask != null) {
			redisSyncTask.cancel();
			return;
		}

	}

	public void onDisable() {
		placeholderManager.unregister();

		for (Building building : Building.getBuildings()) {
			building.stopBuildingTask();
			building.saveInventory();
		}

		for (Player player : getServer().getOnlinePlayers()) {
			BuildingPlayer p = BuildingPlayer.getBuildingPlayer(player);
			p.save();
		}

		stopPlayerSavingTask();
		stopRedisSyncTask();
		BuildingPlayer.getBuildingPlayers().clear();

		getLogger().info("Builder plugin has been disabled!");
	}

	public void startPlayerSavingTask() {
		playerSavingTask = getServer().getScheduler().runTaskTimerAsynchronously(this, () -> {
			for (BuildingPlayer player : BuildingPlayer.getBuildingPlayers()) {
				player.save();
			}
		}, getConfig().getInt("settings.player-saving-interval", 5) * 20, getConfig().getInt("settings.player-saving-interval", 5) * 20);
	}

	public void stopPlayerSavingTask() {
		if (playerSavingTask == null) {
			return;
		}

		playerSavingTask.cancel();
	}

	public static LocaleManager getLocaleManager() {
		return localeManager;
	}

	public static JedisConnection getJedisConnection() {
		return jedisConnection;
	}

	public static FileManager getPlayerData() {
		return playerData;
	}

	public static TiszaBuilder getInstance() {
		return instance;
	}

	public static Config getConfiguration() {
		return config;
	}

	public static PlaceholderManager getPlaceholderManager() {
		return placeholderManager;
	}

	public static File getPluginDataFolder() {
		return pluginDataFolder;
	}

}
