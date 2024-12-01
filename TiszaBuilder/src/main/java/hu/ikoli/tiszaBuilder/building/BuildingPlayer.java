package hu.ikoli.tiszabuilder.building;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import hu.ikoli.tiszabuilder.TiszaBuilder;
import hu.ikoli.tiszabuilder.config.Config;
import hu.ikoli.tiszabuilder.utils.FileManager;

public class BuildingPlayer {

    private static List<BuildingPlayer> buildingPlayers = new ArrayList<BuildingPlayer>();
    private static FileManager playerData = TiszaBuilder.getPlayerData();

    private Player player;

    private PlayerStats playerStats;

    private Map<String, Integer> placedBuildingBlocks = new HashMap<String, Integer>();

    public BuildingPlayer(Player player) {
        this.player = player;

        if (Config.isBuildingServer()) {
            String node = "players." + player.getName() + ".";

            for (String blocks : playerData.getConfig().getStringList(node + "placed-blocks-type")) {
                String[] block = blocks.split(":");
                placedBuildingBlocks.put(block[0], Integer.parseInt(block[1]));
            }
        } else {
            playerStats = new PlayerStats(player.getName());
        }

        buildingPlayers.add(this);
    }

    @Override
    public String toString() {
        return player.getName();
    }

    public PlayerStats getPlayerStats() {
        return playerStats;
    }

    public void addPlacedBlock(ItemStack item) {
        String block = item.getType().name();
        if (placedBuildingBlocks.containsKey(block)) {
            placedBuildingBlocks.put(block, placedBuildingBlocks.get(block) + item.getAmount());
        } else {
            placedBuildingBlocks.put(block, item.getAmount());
        }
    }

    public Player getPlayer() {
        return player;
    }

    public int getBlocksPlaced() {
        return placedBuildingBlocks.values().stream().mapToInt(Integer::intValue).sum();
    }

    public int getBlocksPlaced(String block) {
        return placedBuildingBlocks.get(block);
    }

    public Map<String, Integer> getPlacedBuildingBlocks() {
        return placedBuildingBlocks;
    }

    public void save() {
        List<String> blocks = new ArrayList<String>();
        for (String block : placedBuildingBlocks.keySet()) {
            blocks.add(block + ":" + placedBuildingBlocks.get(block));
        }
        playerData.getConfig().set("players." + player.getName() + ".placed-blocks-type", blocks);

        playerData.saveConfig();
    }

    public static int getContributorsCount() {
        ConfigurationSection players = playerData.getConfig().getConfigurationSection("players");
        if (players == null) {
            return 0;
        }

        int sum = 0;

        for (String player : players.getKeys(false)) {
            if (getPlacedBlocksCount(player) > 0) {
                sum++;
            }
        }

        return sum;
    }

    public static List<BuildingPlayer> getBuildingPlayers() {
        return new ArrayList<BuildingPlayer>(buildingPlayers);
    }

    public static BuildingPlayer getBuildingPlayer(Player player) {
        for (BuildingPlayer buildingPlayer : getBuildingPlayers()) {
            if (buildingPlayer.getPlayer().getName().equals(player.getName())) {
                return buildingPlayer;
            }
        }

        return new BuildingPlayer(player);
    }

    public static int getPlacedBlocksCount(String player) {
        int sum = 0;
        for (String block : playerData.getConfig().getStringList("players." + player + ".placed-blocks-type")) {
            String[] blockData = block.split(":");
            int blockCount = Integer.parseInt(blockData[1]);
            sum += blockCount;
        }

        return sum;
    }

    public static int getContrubotorPlace(String player) {
        if (playerData.getConfig().getConfigurationSection("players") == null) {
            return 0;
        }

        int place = 1;
        for (String p : playerData.getConfig().getConfigurationSection("players").getKeys(false)) {
            if (getPlacedBlocksCount(p) > getPlacedBlocksCount(player)) {
                place++;
            }
        }

        return place;
    }

    public static Map<String, Integer> getPlacedToplist() {

        Map<String, Integer> toplist = new HashMap<String, Integer>();
        for (String key : playerData.getConfig().getConfigurationSection("players").getKeys(false)) {
            int blocks = getPlacedBlocksCount(key);
            if (blocks == 0) {
                continue;
            }
            toplist.put(key, blocks);
        }

        // Sort the map by value
        Map<String, Integer> sortedToplist = toplist.entrySet().stream().sorted(Map.Entry.<String, Integer>comparingByValue().reversed()).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, HashMap::new));

        return sortedToplist;
    }

}
