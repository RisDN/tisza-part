package hu.ikoli.tiszabuilder.config;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import hu.ikoli.tiszabuilder.TiszaBuilder;
import hu.ikoli.tiszabuilder.utils.Utils;

public class Config {

    private static File folder;
    private static YamlConfiguration config;

    public Config() {
        folder = new File(TiszaBuilder.getPluginDataFolder(), "config.yml").toPath().getParent().toFile();
        setDefaultConfig();
    }

    public static String getMessage(String node) {
        if (config.getString("messages." + node) != null) {
            return Utils.color(config.getString("messages." + node)).replace("%prefix%", getPrefix());
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
        setConfig("server-type", "building");

        setConfig("messages.no-permission", "%prefix% &cNincs jogosultságod ehhez!");
        setConfig("messages.reloaded", "%prefix% &aConfig újratöltve!");
        setConfig("messages.block-no-longer-needed", "%prefix% &cEz a blokk már nem szükséges az épülethez! &7(%block%)");
        setConfig("messages.block-not-needed", "%prefix% &cEz a blokk nem szükséges az épülethez! &7(%block%)");
        setConfig("messages.no-buildings", "%prefix% &cJelenleg nics megépítendő épület!");
        setConfig("messages.not-accesible-page", "%prefix% &cNem létező oldal!");
        setConfig("messages.building-finished", "%prefix% &aAz épület elkészült!");
        setConfig("messages.items-list.header", "&aAz épülethez szükséges blokkok:");
        setConfig("messages.items-list.item", "&7%material% &8- &e%amount%");
        setConfig("messages.items-list.next", "&7[&eKövetkező&7]");
        setConfig("messages.items-list.back", "&7[&cVissza&7]");
        setConfig("messages.items-list.page", "&7[&e%page%&7]");
        setConfig("messages.items-list.current-page", "&7[&e&l%page%&7]");

        setConfig("settings.player-saving-interval", 30);
        setConfig("settings.building-delay", 3);

        setConfig("settings.redis.channel-name", "tiszabuilder");
        setConfig("settings.redis.host", "redis");
        setConfig("settings.redis.port", 6379);
        setConfig("settings.redis.user", "default");
        setConfig("settings.redis.password", "password");
        setConfig("settings.redis.sync-interval", 5);

        saveConfig();
    }

    public static ServerType getServerType() {
        return ServerType.valueOf(config.getString("server-type").toUpperCase());
    }

    public static boolean isBuildingServer() {
        return getServerType().equals(ServerType.BUILDING);
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
        return Utils.color(config.getString("prefix"));
    }

    public static List<Integer> getIntegerList(String node) {
        return config.getIntegerList(node);
    }

    public static List<String> getStringList(String node) {
        return config.getStringList(node);
    }

    public static ConfigurationSection getConfigurationSection(String node) {
        return config.getConfigurationSection(node);
    }

    public static int getInt(String node) {
        return config.getInt(node);
    }

    public static double getDouble(String node) {
        return config.getDouble(node);
    }

    public static String getString(String node) {
        return config.getString(node);
    }

    public static boolean getBoolean(String node) {
        return config.getBoolean(node);
    }

    private static void setConfig(String node, Object value) {
        config.set(node, config.get(node, value));
    }

}
