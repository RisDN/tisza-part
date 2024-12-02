package hu.ris.tiszasafeworld;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import hu.ris.tiszasafeworld.listeners.CommandListener;
import hu.ris.tiszasafeworld.listeners.PlayerJoinListener;
import net.md_5.bungee.api.ChatColor;

public class TiszaSafeWorld extends JavaPlugin {

    private static TiszaSafeWorld instance;

    private static World world;
    private static long before;

    @Override
    public void onEnable() {
        instance = this;

        CommandListener commandListener = new CommandListener();
        getCommand("tiszasafeworld").setExecutor(commandListener);

        saveDefaultConfig();

        world = getServer().getWorld(getConfig().getString("teleport.world"));
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

    public void setNow(String world) {
        getConfig().set("teleport.before", System.currentTimeMillis());
        getConfig().set("teleport.world", world);
        saveConfig();

        reloadData();
    }

    public void reloadData() {
        world = getServer().getWorld(getConfig().getString("teleport.world"));
        before = getConfig().getLong("teleport.before");
    }

    public static World getWorld() {
        return world;
    }

    public static long getBefore() {
        return before;
    }

    public static void eraseSavedPlayers() {
        getInstance().getConfig().set("players", null);
        getInstance().saveConfig();
    }

    public static boolean isLoggedInAfter(Player player) {
        return getInstance().getConfig().getBoolean("players." + player.getName() + ".is-joined", false);
    }

    public static void setLoggedInAfter(Player player, boolean value) {
        getInstance().getConfig().set("players." + player.getName() + ".is-joined", value);
        getInstance().saveConfig();
    }

    private static final Pattern pattern = Pattern.compile("#[a-fA-F0-9]{6}");

    public static List<String> color(List<String> messages) {
        for (int i = 0; i < messages.size(); i++) {
            messages.set(i, color(messages.get(i)));
        }
        return messages;
    }

    public static String color(String message) {
        Matcher match = pattern.matcher(message);
        while (match.find()) {
            if (message.charAt(match.start() - 1) == '&') {
                String color = message.substring(match.start(), match.end());
                message = message.replace("&" + color, ChatColor.of(color) + "");
                match = pattern.matcher(message);
            }
        }
        return ChatColor.translateAlternateColorCodes('&', message);
    }

}