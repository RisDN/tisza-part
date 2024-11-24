package hu.ikoli.tiszabuilder.building;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Particle;

public class Building extends BuildingConfig {

    private static List<Building> buildings = new ArrayList<>();

    private boolean isBuilt;

    public Building(String fileName) {
        super(fileName);

        buildings.add(this);
    }

    public boolean isBuilt() {
        return isBuilt;
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
