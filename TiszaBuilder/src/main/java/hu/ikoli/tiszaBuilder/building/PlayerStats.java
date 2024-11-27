package hu.ikoli.tiszabuilder.building;

import org.bukkit.Bukkit;

import hu.ikoli.tiszabuilder.TiszaBuilder;
import redis.clients.jedis.Jedis;

public class PlayerStats {

    private static final Jedis jedisConnection = TiszaBuilder.getJedisConnection().getJedis();

    private String player;

    private String building_displayname;
    private String building_filename;
    private double building_progress;
    private int building_blocks_needed;
    private int building_blocks_placed;
    private int building_contributors;
    private int player_blocks_placed;
    private double player_blocks_placed_progress;
    private int player_contrubution_place;

    public PlayerStats(String player) {
        this.player = player;
        this.fetch();
    }

    public void fetch() {
        Bukkit.getScheduler().runTaskAsynchronously(TiszaBuilder.getInstance(),

                new Runnable() {
                    @Override
                    public void run() {
                        building_displayname = jedisConnection.get(player + "." + "building_displayname");
                        building_filename = jedisConnection.get(player + "." + "building_filename");
                        building_progress = Double.parseDouble(jedisConnection.get(player + "." + "building_progress"));
                        building_blocks_needed = Integer.parseInt(jedisConnection.get(player + "." + "building_blocks_needed"));
                        building_blocks_placed = Integer.parseInt(jedisConnection.get(player + "." + "building_blocks_placed"));
                        building_contributors = Integer.parseInt(jedisConnection.get(player + "." + "building_contributors"));
                        player_blocks_placed = Integer.parseInt(jedisConnection.get(player + "." + "player_blocks_placed"));
                        player_blocks_placed_progress = Double.parseDouble(jedisConnection.get(player + "." + "player_blocks_placed_progress"));
                        player_contrubution_place = Integer.parseInt(jedisConnection.get(player + "." + "player_contrubution_place"));
                    }
                });
    }

    public String getBuilding_displayname() {
        return building_displayname;
    }

    public String getBuilding_filename() {
        return building_filename;
    }

    public double getBuilding_progress() {
        return building_progress;
    }

    public int getBuilding_blocks_needed() {
        return building_blocks_needed;
    }

    public int getBuilding_blocks_placed() {
        return building_blocks_placed;
    }

    public int getBuilding_contributors() {
        return building_contributors;
    }

    public int getPlayer_blocks_placed() {
        return player_blocks_placed;
    }

    public double getPlayer_blocks_placed_progress() {
        return player_blocks_placed_progress;
    }

    public int getPlayer_contrubution_place() {
        return player_contrubution_place;
    }

}
