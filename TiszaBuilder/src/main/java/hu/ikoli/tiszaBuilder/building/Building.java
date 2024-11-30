package hu.ikoli.tiszabuilder.building;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.inventory.ItemStack;

import hu.ikoli.tiszabuilder.TiszaBuilder;
import hu.ikoli.tiszabuilder.config.Config;
import hu.ikoli.tiszabuilder.config.ServerType;
import redis.clients.jedis.Jedis;

public class Building extends BuildingConfig {

    private static final Jedis jedis = TiszaBuilder.getJedisConnection().getJedis();
    private static List<Building> buildings = new ArrayList<>();

    private Map<Material, Integer> placedBlocks = new HashMap<Material, Integer>();

    private List<ItemStack> inventory = new ArrayList<>();

    private boolean isBuilt;

    private int buildingTaskId;
    private int saveTaskId;
    private int calcTaskId;

    private int placedBlockCount;

    public Building(String fileName) {
        super(fileName);
        placedBlockCount = 0;
        if (getConfig().getConfigurationSection("inventory") == null) {
            getConfig().createSection("inventory");
            saveConfig();
        }

        for (String key : getConfig().getConfigurationSection("inventory").getKeys(false)) {
            Material material = Material.getMaterial(key);
            int amount = getConfig().getInt("inventory." + key);
            ItemStack item = new ItemStack(material, amount);
            inventory.add(item);
            System.out.println("Added " + item.getType().toString() + " to inventory");
        }

        startBuildingTask();
        buildings.add(this);
    }

    public void syncStatsToRedis() {

        if (!Config.getServerType().equals(ServerType.BUILDING)) {
            return;
        }

        Bukkit.getScheduler().runTaskAsynchronously(TiszaBuilder.getInstance(),

                new Runnable() {
                    @Override
                    public void run() {

                        jedis.set("building_displayname", getDisplayname());
                        jedis.set("building_filename", getFileName());
                        jedis.set("building_progress", String.valueOf(getProgress()));
                        jedis.set("building_blocks_needed", String.valueOf(getAllBlocksRequiredCount()));
                        jedis.set("building_blocks_placed", String.valueOf(getPlacedBlocksCount()));
                        jedis.set("building_contributors", String.valueOf(BuildingPlayer.getContributorsCount()));

                        for (BuildingPlayer buildingPlayer : BuildingPlayer.getBuildingPlayers()) {
                            String node = buildingPlayer.getPlayer().getName() + ".";
                            jedis.set(node + "player_blocks_placed", String.valueOf(buildingPlayer.getBlocksPlaced()));
                            jedis.set(node + "player_blocks_placed_progress", String.valueOf(getProgress(buildingPlayer)));
                            jedis.set(node + "player_contrubution_place", String.valueOf(BuildingPlayer.getContrubotorPlace(buildingPlayer.getPlayer().getName())));
                        }
                    }
                });
    }

    public void saveInventory() {
        reloadConfig();
        if (inventory.size() == 0) {
            getConfig().set("inventory", null);
            saveConfig();
            return;
        }
        Map<Material, Integer> items = new HashMap<>();
        for (ItemStack item : inventory) {
            Material material = item.getType();
            int amount = item.getAmount();
            if (items.containsKey(item.getType())) {
                items.put(material, items.get(material) + amount);
            } else {
                items.put(material, amount);
            }
        }

        for (Material material : items.keySet()) {
            getConfig().set("inventory." + material.toString(), items.get(material));
        }

        saveConfig();
    }

    public void startBuildingTask() {
        long delay = (long) Config.getDouble("settings.building-delay") * 20;
        buildingTaskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(TiszaBuilder.getInstance(), new Runnable() {
            @Override
            public void run() {
                build();
            }
        }, delay, delay);

        saveTaskId = Bukkit.getScheduler().runTaskTimerAsynchronously(TiszaBuilder.getInstance(), new Runnable() {
            @Override
            public void run() {
                saveInventory();
            }
        }, 0, 20 * 60).getTaskId();

        for (SchemBlock schemBlock : getRequiredBlocks()) {
            if (!schemBlock.isPlaced()) {
                continue;
            }
            if (placedBlocks.containsKey(schemBlock.getMaterial())) {
                placedBlocks.put(schemBlock.getMaterial(), placedBlocks.get(schemBlock.getMaterial()) + 1);
            } else {
                placedBlocks.put(schemBlock.getMaterial(), 1);
            }
        }
        checkPlacedBlocks();
    }

    public void checkPlacedBlocks() {
        calcTaskId = Bukkit.getScheduler().runTaskTimerAsynchronously(TiszaBuilder.getInstance(), new Runnable() {
            @Override
            public void run() {
                int blocks = 0;
                for (SchemBlock schemBlock : getRequiredBlocks()) {
                    if (!schemBlock.isPlaced()) {
                        continue;
                    }
                    blocks++;
                }
                placedBlockCount = blocks;
            }
        }, 0, 20).getTaskId();
    }

    public int getPlacedBlocksCount() {
        return placedBlockCount;
    }

    public double getProgress() {
        return (double) getPlacedBlocksCount() / getAllBlocksRequiredCount() * 100;
    }

    public double getProgress(BuildingPlayer player) {
        return (double) player.getBlocksPlaced() / getAllBlocksRequiredCount() * 100;
    }

    public int getAllBlocksRequiredCount() {
        return getRequiredBlocks().size();
    }

    public void build() {
        if (inventory.size() == 0) {
            return;
        }

        ItemStack item = inventory.get(0);
        SchemBlock schemBlock = getNextBlock(item.getType());
        if (schemBlock == null) {
            inventory.remove(item);
            return;
        }
        Location nextBlockLocation = schemBlock.getLocation();
        Block block = getWorld().getBlockAt(nextBlockLocation);
        block.setType(item.getType());
        BlockState blockState = block.getState();
        blockState.setBlockData(schemBlock.getBlockData());
        blockState.update(true);

        removeBlock(nextBlockLocation);
        if (placedBlocks.containsKey(item.getType())) {
            placedBlocks.put(item.getType(), placedBlocks.get(item.getType()) + 1);
        } else {
            placedBlocks.put(item.getType(), 1);
        }

        if (item.getAmount() == 1) {
            inventory.remove(item);
        } else {
            item.setAmount(item.getAmount() - 1);
        }

    }

    public SchemBlock getNextBlock(Material material) {
        for (SchemBlock schemBlock : getRequiredBlocks()) {
            if (schemBlock.getMaterial() == material && !schemBlock.isPlaced()) {
                return schemBlock;
            }
        }
        return null;
    }

    public void removeBlock(Location location) {
        for (SchemBlock schemBlock : getRequiredBlocks()) {
            int x = schemBlock.getLocation().getBlockX();
            int y = schemBlock.getLocation().getBlockY();
            int z = schemBlock.getLocation().getBlockZ();
            if (x == location.getBlockX() && y == location.getBlockY() && z == location.getBlockZ()) {
                getRequiredBlocks().remove(schemBlock);
                return;
            }
        }
    }

    public void stopBuildingTask() {
        Bukkit.getScheduler().cancelTask(buildingTaskId);
        Bukkit.getScheduler().cancelTask(saveTaskId);
        Bukkit.getScheduler().cancelTask(calcTaskId);
    }

    public void addItem(ItemStack item) {
        inventory.add(item);
    }

    public boolean isBuilt() {
        return isBuilt;
    }

    public boolean isBlockNeeded(Material material) {
        return getBlocksNeeded().containsKey(material);
    }

    public Map<Material, Integer> getBlocksNeeded() {
        Map<Material, Integer> blocksNeeded = new HashMap<>();
        for (Entry<Material, Integer> block : getAllBlocksNeeded().entrySet()) {
            if (placedBlocks.containsKey(block.getKey())) {
                int needed = block.getValue() - placedBlocks.get(block.getKey());
                if (needed > 0) {
                    blocksNeeded.put(block.getKey(), needed);
                }
            } else {
                blocksNeeded.put(block.getKey(), block.getValue());
            }
        }
        return blocksNeeded;
    }

    public void setBuilt(boolean built) {
        isBuilt = built;
    }

    public void displayParticlesOnBlocks() {
        Bukkit.getLogger().info(getRequiredBlocks().get(0).getLocation().toString());
        for (SchemBlock schemBlock : getRequiredBlocks()) {
            getWorld().spawnParticle(Particle.END_ROD, schemBlock.getLocation(), 1, 0, 0, 0, 0);
        }
    }

    public static List<Building> getBuildings() {
        return new ArrayList<>(buildings);
    }

    public static List<String> getBuildingNames() {
        List<String> names = new ArrayList<>();
        for (Building building : buildings) {
            names.add(building.getFileName());
        }
        return new ArrayList<>(names);
    }

    public static Building getBuilding(String fileName) {
        for (Building building : buildings) {
            if (building.getFileName().equals(fileName)) {
                return building;
            }
        }
        return null;
    }

}
