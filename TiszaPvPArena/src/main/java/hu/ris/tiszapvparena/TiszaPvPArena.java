package hu.ris.tiszapvparena;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;

import hu.ris.tiszapvparena.listeners.CommandListener;

public class TiszaPvPArena extends JavaPlugin {

    private static TiszaPvPArena instance;

    @Override
    public void onEnable() {
        instance = this;

        CommandListener commandListener = new CommandListener();
        getCommand("tiszapvparena").setExecutor(commandListener);

        saveDefaultConfig();

        getLogger().info("TiszaPvPArena plugin has been enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info("TiszaPvPArena plugin has been disabled!");
    }

    public static TiszaPvPArena getInstance() {
        return instance;
    }

    public static World getWorld() {
        return getInstance().getServer().getWorld(getInstance().getConfig().getString("location.world"));
    }

    public static int getCooldown() {
        return getInstance().getConfig().getInt("cooldown-sec");
    }

    public static List<Location> getSpawnLocations() {
        List<String> spawnLocations = getInstance().getConfig().getStringList("location.points");
        List<Location> locations = new ArrayList<>();
        for (String location : spawnLocations) {
            String[] split = location.split(" ");
            locations.add(new Location(getWorld(), Double.parseDouble(split[0]), Double.parseDouble(split[1]), Double.parseDouble(split[2])));
        }
        return locations;
    }

    public static Location getRandomLocation() {
        List<Location> locations = getSpawnLocations();
        return locations.get((int) (Math.random() * locations.size()));
    }

}