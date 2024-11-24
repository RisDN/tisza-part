package hu.ikoli.tiszabuilder.utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import hu.ikoli.tiszabuilder.TiszaBuilder;

public class FileManager {

    private TiszaBuilder plugin = TiszaBuilder.getInstance();
    private FileConfiguration file = null;
    private File configFile = null;
    private String filename = null;

    public FileManager(String fileName) {
        filename = fileName;
        saveDefaultConfig();
    }

    public void setConfig(String node, Object value) {
        getConfig().set(node, getConfig().get(node, value));
    }

    public void reloadConfig() {
        if (isConfigFileNull()) {
            this.configFile = new File(this.plugin.getDataFolder(), filename);
        }

        this.file = YamlConfiguration.loadConfiguration(this.configFile);

        InputStream defaultStream = this.plugin.getResource(filename);
        if (defaultStream != null) {
            YamlConfiguration defaultConfig = YamlConfiguration.loadConfiguration(new InputStreamReader(defaultStream));
            this.file.setDefaults(defaultConfig);
        }
    }

    public FileConfiguration getConfig() {
        if (isFileNull()) {
            reloadConfig();
        }
        return this.file;
    }

    public void saveConfig() {
        if (areAnyFileNull()) {
            return;
        }
        try {
            this.getConfig().save(this.configFile);
        } catch (IOException e) {
            Utils.log("Nem menthető config ide: " + this.configFile.getName());
        }
    }

    public void saveDefaultConfig() {
        if (isConfigFileNull())
            this.configFile = new File(this.plugin.getDataFolder(), filename);

        if (!this.configFile.exists()) {
            this.plugin.saveResource(filename, false);
        }
    }

    public boolean isFileNull() {
        return this.file == null;
    }

    public boolean isConfigFileNull() {
        return this.configFile == null;
    }

    public boolean areAnyFileNull() {
        return isConfigFileNull() || isFileNull();
    }
}
