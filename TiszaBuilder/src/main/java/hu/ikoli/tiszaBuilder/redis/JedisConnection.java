package hu.ikoli.tiszabuilder.redis;

import org.bukkit.Bukkit;

import hu.ikoli.tiszabuilder.config.Config;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;

public class JedisConnection {

    private Jedis jedis;
    private String channelName;

    public JedisConnection() {

        this.channelName = Config.getString("settings.redis.channel-name");
        this.jedis = new Jedis(Config.getString("settings.redis.host"), Config.getInt("settings.redis.port"));

        this.jedis.auth(Config.getString("settings.redis.user"), Config.getString("settings.redis.password"));

        if (this.jedis.isConnected()) {
            Bukkit.getLogger().info("Redis kapcsolat létrehozva.");
        } else {
            Bukkit.getLogger().severe("Nem sikerült létrehozni a Redis kapcsolatot.");
        }

    }

    public Jedis getJedis() {
        return jedis;
    }

    public void sendMessage(String message) {
        jedis.publish(channelName, message);
    }

    public void close() {
        jedis.close();
    }

    private class JedisListener extends JedisPubSub {

        @Override
        public void onMessage(String channel, String message) {
            if (!channel.equals(channelName)) {
                return;
            }

            System.out.println("Üzenet: " + message);
        }
    }

}
