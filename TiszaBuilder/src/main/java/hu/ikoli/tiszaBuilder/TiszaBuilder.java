package hu.ikoli.tiszabuilder;

import java.io.File;

import org.bukkit.plugin.java.JavaPlugin;

import hu.ikoli.tiszabuilder.building.Building;
import hu.ikoli.tiszabuilder.config.Config;
import hu.ikoli.tiszabuilder.listeners.CommandListener;
import hu.ikoli.tiszabuilder.listeners.InventoryCloseListener;

public class TiszaBuilder extends JavaPlugin {

	private static TiszaBuilder instance;
	private static Config config;
	private static PlaceholderManager placeholderManager;
	private static File pluginDataFolder;

	public void onEnable() {
		instance = this;

		CommandListener commandListener = new CommandListener();
		getCommand("builder").setExecutor(commandListener);
		getServer().getPluginManager().registerEvents(commandListener, instance);

		getServer().getPluginManager().registerEvents(new InventoryCloseListener(), this);

		pluginDataFolder = getDataFolder();
		config = new Config();
		placeholderManager = new PlaceholderManager();

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

		getLogger().info("Builder plugin has been enabled!");
	}

	public void onDisable() {
		placeholderManager.unregister();

		for (Building building : Building.getBuildings()) {
			building.stopBuildingTask();
		}

		getLogger().info("Builder plugin has been disabled!");
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
