package hu.ikoli.tiszabuilder.redis;

import hu.ikoli.tiszabuilder.config.Config;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;

public class JedisConnection {

    private Jedis jedis;
    private String channelName;

    public JedisConnection() {

        this.channelName = Config.getString("settings.redis.channel-name");
        this.jedis = new Jedis(Config.getString("settings.redis.host"), Config.getInt("settings.redis.port"));

        new Thread(() -> {
            jedis.subscribe(new JedisListener(), channelName);
        }).start();

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
