package hu.ikoli.tiszabuilder.building;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.type.Stairs.Shape;

public class SchemBlock {

    private Location location;
    private Material material;
    private BlockFace blockFace;
    private Shape shape;

    public SchemBlock(Location location, Material material, BlockFace blockFace, Shape shape) {

        if (material.isAir()) {
            return;
        }

        this.location = location;
        this.material = material;
        this.blockFace = blockFace;
        this.shape = shape;
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

    public Shape getShape() {
        return shape;
    }
}
