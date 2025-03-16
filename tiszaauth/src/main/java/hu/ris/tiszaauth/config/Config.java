package hu.ris.tiszaauth.config;

import java.io.File;
import java.io.IOException;
import java.util.List;
import org.bspfsystems.yamlconfiguration.configuration.ConfigurationSection;
import org.bspfsystems.yamlconfiguration.configuration.InvalidConfigurationException;
import org.bspfsystems.yamlconfiguration.file.YamlConfiguration;

import hu.ris.tiszaauth.Utils;
import net.kyori.adventure.text.Component;

public class Config {

    private static File folder;
    private static YamlConfiguration config;

    public Config() {
        folder = new File("plugins/tiszaauth", "config.yml").toPath().getParent().toFile();
        loadDefaultConfig();
    }

    private static void loadDefaultConfig() {
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

        setDefaultConfig("mysql.host", "127.0.0.1");
        setDefaultConfig("mysql.port", 3306);
        setDefaultConfig("mysql.dbname", "tisza");
        setDefaultConfig("mysql.user", "root");
        setDefaultConfig("mysql.pass", "password");
        setDefaultConfig("mysql.table", "links");

        setDefaultConfig("prefix", "<gray>[<gold>TiszaAuth<gray>] ");
        setDefaultConfig("language.reload", "<green>Sikeresen újratöltötted a konfigurációt!");
        setDefaultConfig("language.no-lobby", "<red>Nincs beállítva lobby!");

        setDefaultConfig("messages.twofa-required", "<red>A rangod alapján a bejelentkezéshez kétlépcsős azonosítás szükséges! Kérlek Discordon használd a /setip parancsot!");
        setDefaultConfig("messages.twofa-not-matching", "<red>Az IP címed nem egyezik meg a beállítottal! Kérlek használd a /setip parancsot Discordon a bejelentkezéshez!");
        setDefaultConfig("messages.not_linked", "<red>Ez a felhasználó nincs összekapcsolva a Discord fiókoddal. Kérlek csatlakozz a Discord szerverünkhöz és kövesd az instrukciókat! https://discord.gg/YjBkzgCZtx");

        saveConfig();
    }

    public static void reloadConfig() {
        File file = new File(folder, "config.yml");
        if (!file.getParentFile().isDirectory()) {
            loadDefaultConfig();
            return;
        }

        try {
            config = new YamlConfiguration();
            config.load(file);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }

    public static Component getMessage(String node) {
        return Utils.color(config.getString("language." + node).replace("%prefix%", getPrefix() + "<reset>"));
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
        return config.getString("prefix");
    }

    private static void setDefaultConfig(String node, Object value) {
        getYamlConfiguration().set(node, getYamlConfiguration().get(node, value));
    }

    public static YamlConfiguration getYamlConfiguration() {
        return config;
    }

    public static int getInt(String node) {
        return config.getInt(node);
    }

    public static int getInt(String node, int defa) {
        return config.getInt(node, defa);
    }

    public static String getString(String node) {
        return config.getString(node);
    }

    public static String getString(String node, String defa) {
        return config.getString(node, defa);
    }

    public static boolean getBoolean(String node) {
        return config.getBoolean(node);
    }

    public static ConfigurationSection getConfigurationSection(String node) {
        return config.getConfigurationSection(node);
    }

    public static List<String> getStringList(String node) {
        return config.getStringList(node);
    }
}