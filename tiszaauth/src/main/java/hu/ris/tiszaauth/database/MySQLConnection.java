package hu.ris.tiszaauth.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import hu.ris.tiszaauth.TiszaAuth;

public class MySQLConnection {

    TiszaAuth plugin = TiszaAuth.getInstance();

    private String mysql_host = plugin.getConfig().getString("mysql.host");
    private String mysql_port = plugin.getConfig().getString("mysql.port");
    private String mysql_dbname = plugin.getConfig().getString("mysql.dbname");
    private String mysql_user = plugin.getConfig().getString("mysql.user");
    private String mysql_pass = plugin.getConfig().getString("mysql.pass");
    private String mysql_table = plugin.getConfig().getString("mysql.table");

    private Connection db;

    public MySQLConnection() {
        plugin.getLogger().info("Host: " + mysql_host + ", Port: " + mysql_port + ", DB: " + mysql_dbname + ", User: " + mysql_user + ", Table: " + mysql_table);
        connectMysql();

    }

    public Connection connection() throws SQLException {
        if (db == null || db.isClosed()) {
            try {
                db = DriverManager.getConnection("jdbc:mysql://" + mysql_host + ":" + mysql_port + "/" + mysql_dbname + "?autoReconnect=true&useSSL=false", mysql_user, mysql_pass);
            } catch (SQLException e) {
                plugin.getLogger().severe("Failed to connect to MySQL database! Shutting down server...");
                plugin.getServer().shutdown();
                e.printStackTrace();
            }
        }
        return db;
    }

    public boolean isConnected() {
        return (db == null ? false : true);
    }

    public void disconnect() {
        if (isConnected()) {
            try {
                db.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void connectMysql() {
        plugin.getServer().getScheduler().runTaskTimerAsynchronously(plugin, () -> {
            try {
                connection().isValid(3000);
            } catch (SQLException e) {
                new RuntimeException("Failed to update db connection!", e).printStackTrace();
            }
        }, 6000L, 6000L);
    }

    public String getTable() {
        return mysql_table;
    }
}
