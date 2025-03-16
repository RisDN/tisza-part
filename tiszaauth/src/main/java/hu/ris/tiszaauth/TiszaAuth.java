package hu.ris.tiszaauth;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Logger;

import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.ProxyServer;

import hu.ris.tiszaauth.config.Config;
import hu.ris.tiszaauth.database.MySQLConnection;
import hu.ris.tiszaauth.listeners.PlayerJoinListener;
import hu.ris.tiszaauth.listeners.ProxyConnectListener;

@Plugin(id = "tiszaauth", name = "TiszaAuth", version = "1.0-SNAPSHOT", authors = { "Ris" })
public class TiszaAuth {

    private static TiszaAuth instance;
    private static MySQLConnection mysqlConnection;

    private final ProxyServer server;
    private final Logger logger;

    @Inject
    public TiszaAuth(ProxyServer server, Logger logger) {
        instance = this;
        this.server = server;
        this.logger = logger;
    }

    private void init() {
        new Config();
        mysqlConnection = new MySQLConnection();

        server.getEventManager().register(this, new PlayerJoinListener());
        server.getEventManager().register(this, new ProxyConnectListener());
        logger.info("Tisza Auth started.");
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        init();
    }

    public static TiszaAuth getInstance() {
        return instance;
    }

    public static ProxyServer getServer() {
        return instance.server;
    }

    public static Logger getLogger() {
        return instance.logger;
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

    public static String getSavedIp(String playername) {
        try {
            PreparedStatement statement = makeStatement("SELECT * FROM `" + getMySQLConnection().getTable() + "` WHERE `username` = ? LIMIT 1;");
            statement.setString(1, playername.toLowerCase());
            ResultSet result = statement.executeQuery();

            if (result.next()) {
                String ip = result.getString("ip");
                statement.close();
                return ip;
            }

            statement.close();
            return null;

        } catch (Exception e) {
            throw new RuntimeException("Failed to get saved ip!", e);
        }
    }

}