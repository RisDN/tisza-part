package hu.ris.tiszaauth;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.bukkit.plugin.java.JavaPlugin;

import hu.ris.tiszaauth.database.MySQLConnection;
import hu.ris.tiszaauth.listeners.PlayerJoinListener;

public class TiszaAuth extends JavaPlugin {

    private static TiszaAuth instance;
    private static MySQLConnection mysqlConnection;

    @Override
    public void onEnable() {
        instance = this;

        saveDefaultConfig();
        mysqlConnection = new MySQLConnection();
        getServer().getPluginManager().registerEvents(new PlayerJoinListener(), instance);
        getLogger().info("Tisza Auth started.");
    }

    @Override
    public void onDisable() {
        getLogger().info("Tisza Auth stopped.");
    }

    public static TiszaAuth getInstance() {
        return instance;
    }

    public static MySQLConnection getMySQLConnection() {
        return mysqlConnection;
    }

    public static PreparedStatement makeStatement(String statement) throws SQLException {
        return getMySQLConnection().connection().prepareStatement(statement);
    }

    public static boolean isPlayerLinked(String playerName) {
        try {
            PreparedStatement statement = makeStatement("SELECT * FROM `" + getMySQLConnection().getTable() + "` WHERE `username` = ? LIMIT 1;");
            statement.setString(1, playerName.toLowerCase());
            ResultSet result = statement.executeQuery();

            boolean isLinked = result.next();
            statement.close();
            return isLinked;

        } catch (Exception e) {
            throw new RuntimeException("Failed to check if player is linked!", e);
        }

    }

}