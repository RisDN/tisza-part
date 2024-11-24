package hu.ikoli.tiszaBuilder;

import org.bukkit.plugin.java.JavaPlugin;

import hu.ikoli.tiszaBuilder.config.Config;
import hu.ikoli.tiszaBuilder.listeners.CommandListener;

public class TiszaBuilder extends JavaPlugin {

  private static TiszaBuilder instance;
  private static Config config;
  private static PlaceholderManager placeholderManager;

  public TiszaBuilder() {
    instance = this;
    config = new Config();
    placeholderManager = new PlaceholderManager();
  }

  public void onEnable() {
    CommandListener commandListener = new CommandListener();
    getCommand("builder").setExecutor(commandListener);
    getServer().getPluginManager().registerEvents(commandListener, instance);
    getLogger().info("Builder plugin has been enabled!");
  }

  public void onDisable() {
    placeholderManager.unregister();
    getLogger().info("Builder plugin has been disabled!");
  }

  public static TiszaBuilder getInstance() {
    return instance;
  }

  public static Config getConfiguration() {
    return config;
  }

}
