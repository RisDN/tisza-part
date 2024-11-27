package hu.ikoli.tiszabuilder.building;

import org.bukkit.Bukkit;

import hu.ikoli.tiszabuilder.TiszaBuilder;
import redis.clients.jedis.Jedis;

public class PlayerStats {

    private static final Jedis jedisConnection = TiszaBuilder.getJedisConnection().getJedis();

    private String player;

    private static String building_displayname;
    private static String building_filename;
    private static double building_progress;
    private static int building_blocks_needed;
    private static int building_blocks_placed;
    private static int building_contributors;
    private int player_blocks_placed;
    private double player_blocks_placed_progress;
    private int player_contrubution_place;

    public PlayerStats(String player) {
        this.player = player;
    }

    public static void fetch() {
        Bukkit.getScheduler().runTaskAsynchronously(TiszaBuilder.getInstance(),

                new Runnable() {
                    @Override
                    public void run() {

                        building_displayname = jedisConnection.get("building_displayname");
                        building_filename = jedisConnection.get("building_filename");
                        building_progress = Double.parseDouble(jedisConnection.get("building_progress"));
                        building_blocks_needed = Integer.parseInt(jedisConnection.get("building_blocks_needed"));
                        building_blocks_placed = Integer.parseInt(jedisConnection.get("building_blocks_placed"));
                        building_contributors = Integer.parseInt(jedisConnection.get("building_contributors"));

                        for (BuildingPlayer player : BuildingPlayer.getBuildingPlayers()) {
                            player.getPlayerStats().player_blocks_placed = Integer.parseInt(jedisConnection.get(player + "." + "player_blocks_placed"));
                            player.getPlayerStats().player_blocks_placed_progress = Double.parseDouble(jedisConnection.get(player + "." + "player_blocks_placed_progress"));
                            player.getPlayerStats().player_contrubution_place = Integer.parseInt(jedisConnection.get(player + "." + "player_contrubution_place"));
                        }
                    }
                });
    }

    public static String getBuilding_displayname() {
        return building_displayname;
    }

    public static String getBuilding_filename() {
        return building_filename;
    }

    public static double getBuilding_progress() {
        return building_progress;
    }

    public static int getBuilding_blocks_needed() {
        return building_blocks_needed;
    }

    public static int getBuilding_blocks_placed() {
        return building_blocks_placed;
    }

    public static int getBuilding_contributors() {
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
