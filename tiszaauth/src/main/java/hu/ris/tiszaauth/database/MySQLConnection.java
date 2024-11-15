package hu.ris.tiszaauth.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import hu.ris.tiszaauth.TiszaAuth;
import hu.ris.tiszaauth.config.Config;

public class MySQLConnection {

    TiszaAuth plugin = TiszaAuth.getInstance();

    private String mysql_host = Config.getString("mysql.host");
    private String mysql_port = Config.getString("mysql.port");
    private String mysql_dbname = Config.getString("mysql.dbname");
    private String mysql_user = Config.getString("mysql.user");
    private String mysql_pass = Config.getString("mysql.pass");
    private String mysql_table = Config.getString("mysql.table");

    private Connection db;

    public MySQLConnection() {
        connectMysql();
    }

    public Connection connection() throws SQLException {
        if (db == null || db.isClosed()) {
            try {
                db = DriverManager.getConnection("jdbc:mysql://" + mysql_host + ":" + mysql_port + "/" + mysql_dbname + "?autoReconnect=true&useSSL=false", mysql_user, mysql_pass);
            } catch (SQLException e) {
                TiszaAuth.getLogger().severe("Failed to connect to MySQL database! Shutting down server...");
                TiszaAuth.getServer().shutdown();
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
        TiszaAuth.getServer().getScheduler().buildTask(plugin, () -> {
            try {
                connection().isValid(3000);
            } catch (SQLException e) {
                new RuntimeException("Failed to update db connection!", e).printStackTrace();
            }
        }).schedule();
    }

    public String getTable() {
        return mysql_table;
    }
}
