package hu.ikoli.tiszaBuilder.config;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import hu.ikoli.tiszaBuilder.TiszaBuilder;
import hu.ikoli.tiszaBuilder.utils.Utils;

public class Config {

    private static TiszaBuilder plugin = TiszaBuilder.getInstance();
    private static File folder;
    private static YamlConfiguration config;

    public Config() {
        setDefaultConfig();
    }

    public static String getMessage(String node) {
        if (plugin.getConfig().getString("messages." + node) != null) {
            return Utils.color(plugin.getConfig().getString("messages." + node)).replace("%prefix%", getPrefix());
        }
        return Utils.color("&cNincs ilyen üzenet a configban: messages." + node);
    }

    private static void setDefaultConfig() {
        File file = new File(folder, "config.yml");
        if (!file.getParentFile().isDirectory()) {
            file.getParentFile().mkdir();
        }

        try {
            if (file.isFile()) {
                config = new YamlConfiguration();
                try {
                    config.load(file);
                } catch (InvalidConfigurationException e) {
                    e.printStackTrace();
                }
            } else {
                config = new YamlConfiguration();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        setConfig("prefix", "&8[&eTiszaBuilder&8] »");

        setConfig("messages.no-permission", "%prefix% &cNincs jogosultságod ehhez!");
        setConfig("messages.reloaded", "%prefix% &aConfig újratöltve!");

    }

    public static void reloadConfig() {
        File file = new File(folder, "config.yml");
        if (!file.getParentFile().isDirectory()) {
            setDefaultConfig();
            return;
        }

        try {
            config = new YamlConfiguration();
            config.load(file);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }

    public static void saveConfig() {
        File file = new File(folder, "config.yml");
        if (!file.getParentFile().isDirectory()) {
            file.getParentFile().mkdir();
        }

        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getPrefix() {
        return Utils.color(plugin.getConfig().getString("prefix"));
    }

    public static List<Integer> getIntegerList(String node) {
        return plugin.getConfig().getIntegerList(node);
    }

    public static List<String> getStringList(String node) {
        return plugin.getConfig().getStringList(node);
    }

    public static ConfigurationSection getConfigurationSection(String node) {
        return plugin.getConfig().getConfigurationSection(node);
    }

    public static int getInt(String node) {
        return plugin.getConfig().getInt(node);
    }

    public static double getDouble(String node) {
        return plugin.getConfig().getDouble(node);
    }

    public static String getString(String node) {
        return plugin.getConfig().getString(node);
    }

    public static boolean getBoolean(String node) {
        return plugin.getConfig().getBoolean(node);
    }

    private static void setConfig(String node, Object value) {
        plugin.getConfig().set(node, plugin.getConfig().get(node, value));
    }

}
