package hu.ikoli.tiszabuilder.building;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.Directional;
import org.bukkit.block.data.type.Stairs;
import org.bukkit.inventory.ItemStack;

import hu.ikoli.tiszabuilder.TiszaBuilder;
import hu.ikoli.tiszabuilder.config.Config;

public class Building extends BuildingConfig {

    private static List<Building> buildings = new ArrayList<>();

    private List<ItemStack> inventory = new ArrayList<>();

    private boolean isBuilt;

    private int buildingTaskId;

    public Building(String fileName) {
        super(fileName);

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

    public void saveInventory() {

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
        buildingTaskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(TiszaBuilder.getInstance(),

                new Runnable() {
                    @Override
                    public void run() {
                        build();
                    }
                }, 5, 5);
    }

    public int getPlacedBlocksCount() {
        int sum = 0;
        for (SchemBlock schemBlock : getAllBlocksNeeded()) {
            if (!schemBlock.isPlaced()) {
                continue;
            }

            sum++;
        }

        return sum;
    }

    public double getProgress() {
        return (double) getPlacedBlocksCount() / getAllBlocksRequiredCount() * 100;
    }

    public double getProgress(BuildingPlayer player) {
        return (double) player.getBlocksPlaced() / getAllBlocksRequiredCount() * 100;
    }

    public int getAllBlocksRequiredCount() {
        return getAllBlocksNeeded().size();
    }

    public void build() {
        if (inventory.size() == 0) {
            return;
        }

        ItemStack item = inventory.get(0);

        inventory.remove(item);
        for (int i = 0; i < item.getAmount(); i++) {
            long delay = i * (long) Config.getDouble("settings.building-delay") * 20;
            Bukkit.getScheduler().runTaskLater(TiszaBuilder.getInstance(),

                    new Runnable() {
                        @Override
                        public void run() {
                            SchemBlock schemBlock = getNextBlock(item.getType());
                            Location nextBlockLocation = schemBlock.getLocation();
                            Block block = getWorld().getBlockAt(nextBlockLocation);
                            block.setType(item.getType());
                            BlockState blockState = block.getState();

                            if (blockState.getBlockData() instanceof Directional directional) {
                                directional.setFacing(schemBlock.getBlockFace());
                                blockState.setBlockData(directional);
                                blockState.update(true);
                            }
                            if (blockState.getBlockData() instanceof Stairs stairs) {
                                stairs.setShape(schemBlock.getShape());
                                blockState.setBlockData(stairs);
                                blockState.update(true);
                            }

                            removeBlock(nextBlockLocation);
                        }
                    }, delay);
        }
    }

    public SchemBlock getNextBlock(Material material) {
        for (SchemBlock schemBlock : getAllBlocksNeeded()) {
            if (schemBlock.getMaterial() == material && !schemBlock.isPlaced()) {
                return schemBlock;
            }
        }
        return null;
    }

    public void removeBlock(Location location) {
        for (SchemBlock schemBlock : getAllBlocksNeeded()) {
            int x = schemBlock.getLocation().getBlockX();
            int y = schemBlock.getLocation().getBlockY();
            int z = schemBlock.getLocation().getBlockZ();
            if (x == location.getBlockX() && y == location.getBlockY() && z == location.getBlockZ()) {
                getAllBlocksNeeded().remove(schemBlock);
                saveInventory();
                return;
            }
        }
    }

    public void stopBuildingTask() {
        Bukkit.getScheduler().cancelTask(buildingTaskId);
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

    public void setBuilt(boolean built) {
        isBuilt = built;
    }

    public void displayParticlesOnBlocks() {
        Bukkit.getLogger().info(getAllBlocksNeeded().get(0).getLocation().toString());
        for (SchemBlock schemBlock : getAllBlocksNeeded()) {
            getWorld().spawnParticle(Particle.END_ROD, schemBlock.getLocation(), 1, 0, 0, 0, 0);
        }
    }

    public static List<Building> getBuildings() {
        return buildings;
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
