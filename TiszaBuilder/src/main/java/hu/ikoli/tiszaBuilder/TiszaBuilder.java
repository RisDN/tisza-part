package hu.ikoli.tiszaBuilder;

import org.bukkit.plugin.java.JavaPlugin;

import hu.ikoli.tiszaBuilder.config.Config;

public class TiszaBuilder extends JavaPlugin {

  private static TiszaBuilder instance;
  private static Config config;

  public TiszaBuilder() {
    instance = this;
    config = new Config();
  }

  public void onEnable() {
    getLogger().info("Builder plugin has been enabled!");
  }

  public void onDisable() {
    getLogger().info("Builder plugin has been disabled!");
  }

  public static TiszaBuilder getInstance() {
    return instance;
  }

  public static Config getConfiguration() {
    return config;
  }

}
