package hu.ikoli.tiszabuilder;

import java.io.File;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import hu.ikoli.tiszabuilder.building.Building;
import hu.ikoli.tiszabuilder.building.BuildingPlayer;
import hu.ikoli.tiszabuilder.config.Config;
import hu.ikoli.tiszabuilder.listeners.CommandListener;
import hu.ikoli.tiszabuilder.listeners.InventoryCloseListener;
import hu.ikoli.tiszabuilder.listeners.PlayerJoinListener;
import hu.ikoli.tiszabuilder.listeners.PlayerQuitListener;
import hu.ikoli.tiszabuilder.utils.FileManager;

public class TiszaBuilder extends JavaPlugin {

	private static TiszaBuilder instance;
	private static Config config;
	private static PlaceholderManager placeholderManager;
	private static File pluginDataFolder;
	private static FileManager playerData;

	private BukkitTask playerSavingTask;

	public void onEnable() {
		instance = this;

		CommandListener commandListener = new CommandListener();
		getCommand("builder").setExecutor(commandListener);
		getServer().getPluginManager().registerEvents(commandListener, instance);

		getServer().getPluginManager().registerEvents(new InventoryCloseListener(), this);

		getServer().getPluginManager().registerEvents(new PlayerJoinListener(), this);
		getServer().getPluginManager().registerEvents(new PlayerQuitListener(), this);

		pluginDataFolder = getDataFolder();
		config = new Config();
		placeholderManager = new PlaceholderManager();
		playerData = new FileManager("playerdata.yml");

		File buildingFolder = new File(getDataFolder(), "buildings");
		if (!buildingFolder.exists()) {
			buildingFolder.mkdirs();
			return;
		}

		for (File file : buildingFolder.listFiles()) {
			if (file.getName().endsWith(".yml")) {
				new Building(file.getName().replace(".yml", ""));
			}
		}

		for (Player player : getServer().getOnlinePlayers()) {
			BuildingPlayer.getBuildingPlayer(player);
		}

		startPlayerSavingTask();

		getLogger().info("Builder plugin has been enabled!");
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
