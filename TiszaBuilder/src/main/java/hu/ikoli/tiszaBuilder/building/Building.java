package hu.ikoli.tiszabuilder.building;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.inventory.ItemStack;

import hu.ikoli.tiszabuilder.TiszaBuilder;

public class Building extends BuildingConfig {

    private static List<Building> buildings = new ArrayList<>();

    private List<ItemStack> inventory = new ArrayList<>();

    private boolean isBuilt;

    private int buildingTaskId;

    public Building(String fileName) {
        super(fileName);

        startBuildingTask();
        buildings.add(this);
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

    public void build() {
        if (inventory.size() == 0) {
            return;
        }

        ItemStack item = inventory.get(0);

        inventory.remove(item);
        for (int i = 0; i < item.getAmount(); i++) {
            long delay = i * 5;
            Bukkit.getScheduler().runTaskLater(TiszaBuilder.getInstance(),

                    new Runnable() {
                        @Override
                        public void run() {
                            Location nextBlockLocation = getNextBlockLocation(item.getType());
                            getWorld().getBlockAt(nextBlockLocation).setType(item.getType());
                            removeBlock(nextBlockLocation);
                        }
                    }, delay);
        }
    }

    public Location getNextBlockLocation(Material material) {
        for (SchemBlock schemBlock : getAllBlocksNeeded()) {
            if (schemBlock.getMaterial() == material && !schemBlock.isPlaced()) {
                return schemBlock.getLocation();
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
