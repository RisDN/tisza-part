package hu.ikoli.tiszabuilder.building;

import org.bukkit.Location;
import org.bukkit.Material;

public class SchemBlock {

    private Location location;
    private Material material;

    public SchemBlock(Location location, Material material) {

        if (material.isAir()) {
            return;
        }

        this.location = location;
        this.material = material;

    }

    public Location getLocation() {
        return location;
    }

    public Material getMaterial() {
        return material;
    }

    public boolean isPlaced() {
        return location.getWorld().getBlockAt(location.getBlockX(), location.getBlockY(), location.getBlockZ()).getType().equals(material);
    }
}
