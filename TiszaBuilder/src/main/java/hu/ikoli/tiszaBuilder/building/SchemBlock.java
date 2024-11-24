package hu.ikoli.tiszabuilder.building;

import javax.annotation.Nullable;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;

public class SchemBlock {

    private Location location;
    private Material material;
    private BlockFace blockFace;

    public SchemBlock(Location location, Material material, @Nullable BlockFace blockFace) {

        if (material.isAir()) {
            return;
        }

        this.location = location;
        this.material = material;
        this.blockFace = blockFace;

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

    public BlockFace getBlockFace() {
        return blockFace;
    }
}
