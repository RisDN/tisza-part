package hu.ikoli.tiszabuilder.building;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;

public class SchemBlock {

    private Location location;
    private BlockData blockData;

    public SchemBlock(Location location, BlockData blockData) throws IllegalArgumentException {
        if (blockData.getMaterial().isAir()) {
            throw new IllegalArgumentException("BlockData cannot be air");
        }

        this.location = location;
        this.blockData = blockData;
    }

    public Location getLocation() {
        return location;
    }

    public Material getMaterial() {
        return blockData.getMaterial();
    }

    public boolean isPlaced() {
        return location.getWorld().getBlockAt(location.getBlockX(), location.getBlockY(), location.getBlockZ()).getType().equals(getMaterial());
    }

    public BlockData getBlockData() {
        return blockData;
    }
}
