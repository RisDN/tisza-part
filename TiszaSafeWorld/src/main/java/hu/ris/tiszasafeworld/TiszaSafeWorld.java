package hu.ris.tiszasafeworld;

import java.util.Arrays;
import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import hu.ris.tiszasafeworld.listeners.CommandListener;
import hu.ris.tiszasafeworld.listeners.PlayerJoinListener;

public class TiszaSafeWorld extends JavaPlugin {

    private static TiszaSafeWorld instance;

    private static List<String> worlds;
    private static long before;

    @Override
    public void onEnable() {
        instance = this;

        CommandListener commandListener = new CommandListener();
        getCommand("tiszasafeworld").setExecutor(commandListener);

        saveDefaultConfig();

        worlds = getConfig().getStringList("teleport.worlds");

        before = getConfig().getLong("teleport.before");

        getServer().getPluginManager().registerEvents(new PlayerJoinListener(), this);
        getLogger().info("TiszaSafeWorld plugin has been enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info("TiszaSafeWorld plugin has been disabled!");
    }

    public static TiszaSafeWorld getInstance() {
        return instance;
    }

    public void setNow(String world1, String world2) {
        getConfig().set("teleport.before", System.currentTimeMillis());
        getConfig().set("teleport.worlds", Arrays.asList(world1, world2));
        saveConfig();

        reloadData();
    }

    public void reloadData() {
        worlds = getConfig().getStringList("teleport.worlds");
        before = getConfig().getLong("teleport.before");
    }

    public static List<String> getWorlds() {
        return worlds;
    }

    public static long getBefore() {
        return before;
    }

    public static void eraseSavedPlayers() {
        getInstance().getConfig().set("players", null);
        getInstance().saveConfig();
    }

    public static boolean isLoggedInAfter(Player player, String worldName) {
        return getInstance().getConfig().getBoolean("players." + player.getName() + "." + worldName, false);
    }

    public static void setLoggedInAfter(Player player, boolean value, String worldName) {
        getInstance().getConfig().set("players." + player.getName() + "." + worldName, value);
        getInstance().saveConfig();
    }

}