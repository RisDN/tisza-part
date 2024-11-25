package hu.ikoli.tiszabuilder.building;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import hu.ikoli.tiszabuilder.TiszaBuilder;
import hu.ikoli.tiszabuilder.utils.FileManager;

public class BuildingPlayer {

    private static List<BuildingPlayer> buildingPlayers = new ArrayList<BuildingPlayer>();
    private static FileManager playerData = TiszaBuilder.getPlayerData();

    private Player player;

    private int blocksPlaced = 0;
    private Map<String, Integer> placedBuildingBlocks = new HashMap<String, Integer>();

    public BuildingPlayer(Player player) {
        this.player = player;
        System.err.println("BuildingPlayer: " + player.getName());
        String node = player.getName() + ".";

        for (String blocks : playerData.getConfig().getStringList(node + "placed-blocks-type")) {
            String[] block = blocks.split(":");
            placedBuildingBlocks.put(block[0], Integer.parseInt(block[1]));
            System.out.println("Loading: " + block[0] + ":" + block[1]);
        }

        this.blocksPlaced = placedBuildingBlocks.values().stream().mapToInt(Integer::intValue).sum();

        buildingPlayers.add(this);
    }

    public void addPlacedBlock(ItemStack item) {
        String block = item.getType().name();
        if (placedBuildingBlocks.containsKey(block)) {
            placedBuildingBlocks.put(block, placedBuildingBlocks.get(block) + item.getAmount());
        } else {
            placedBuildingBlocks.put(block, item.getAmount());
        }

        this.blocksPlaced = placedBuildingBlocks.values().stream().mapToInt(Integer::intValue).sum();
    }

    public static List<BuildingPlayer> getBuildingPlayers() {
        return buildingPlayers;
    }

    public static BuildingPlayer getBuildingPlayer(Player player) {
        for (BuildingPlayer buildingPlayer : buildingPlayers) {
            if (buildingPlayer.getPlayer().getName().equals(player.getName())) {
                return buildingPlayer;
            }
        }

        return new BuildingPlayer(player);
    }

    public Player getPlayer() {
        return player;
    }

    public int getBlocksPlaced() {
        return blocksPlaced;
    }

    public int getBlocksPlaced(String block) {
        return placedBuildingBlocks.get(block);
    }

    public Map<String, Integer> getPlacedBuildingBlocks() {
        return placedBuildingBlocks;
    }

    public void save() {
        System.out.println("Saving player: " + player.getName());
        List<String> blocks = new ArrayList<String>();
        for (String block : placedBuildingBlocks.keySet()) {
            System.out.println("Saving: " + block + ":" + placedBuildingBlocks.get(block));
            blocks.add(block + ":" + placedBuildingBlocks.get(block));
        }
        playerData.getConfig().set(player.getName() + ".placed-blocks-type", blocks);

        playerData.saveConfig();
    }

}
