package hu.ikoli.tiszabuilder.building;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.data.BlockData;
import org.bukkit.configuration.file.FileConfiguration;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.world.block.BlockState;

import hu.ikoli.tiszabuilder.TiszaBuilder;
import hu.ikoli.tiszabuilder.utils.FileManager;
import hu.ikoli.tiszabuilder.utils.Utils;

public abstract class BuildingConfig {

    private String fileName;
    private FileManager config;
    private String displayname;
    private List<SchemBlock> requiredBlocks = new ArrayList<SchemBlock>();
    private Map<Material, Integer> allBlocksNeeded = new HashMap<Material, Integer>();

    public BuildingConfig(String fileName) {
        Bukkit.getLogger().info("Loading building: " + fileName);

        config = new FileManager("buildings/" + fileName + ".yml");
        this.setDefaultConfig();

        this.loadSchematic(new File(TiszaBuilder.getPluginDataFolder(), "schematics/" + fileName + ".schem"), this.getWorld());

        this.fileName = fileName;
        this.displayname = getConfig().getString("displayname");

    }

    private void setDefaultConfig() {
        reloadConfig();

        setConfig("displayname", "Default displayname");
        setConfig("world-name", "world");
        setConfig("starting-position", "-133 83 -506");

        saveConfig();
    }

    public World getWorld() {

        World world = Bukkit.getWorld(getConfig().getString("world-name"));

        if (world == null) {
            throw new IllegalArgumentException("World does not exist named " + getConfig().getString("world-name"));
        }

        return world;
    }

    public Location getStartingPosition() {
        String[] pos = getConfig().getString("starting-position").split(" ");
        return new Location(getWorld(), Double.parseDouble(pos[0]), Double.parseDouble(pos[1]), Double.parseDouble(pos[2]));
    }

    public String getFileName() {
        return fileName;
    }

    public String getDisplayname() {
        return Utils.color(displayname);
    }

    public List<SchemBlock> getRequiredBlocks() {
        return new ArrayList<>(requiredBlocks);
    }

    public Map<Material, Integer> getAllBlocksNeeded() {
        return new HashMap<>(allBlocksNeeded);
    }

    public FileConfiguration getConfig() {
        return config.getConfig();
    }

    public void saveConfig() {
        config.saveConfig();
    }

    public void reloadConfig() {
        config.reloadConfig();
    }

    private void setConfig(String node, Object value) {
        getConfig().set(node, getConfig().get(node, value));
    }

    public void loadSchematic(File schematicFile, World world) {
        try {
            if (!schematicFile.exists()) {
                throw new IllegalArgumentException("Schematic file does not exist: " + schematicFile.getAbsolutePath());
            }

            ClipboardFormat format = ClipboardFormats.findByFile(schematicFile);
            if (format == null) {
                throw new IllegalArgumentException("Unsupported schematic format: " + schematicFile.getAbsolutePath());
            }

            try (ClipboardReader reader = format.getReader(new FileInputStream(schematicFile))) {
                Clipboard clipboard = reader.read();

                BlockVector3 origin = clipboard.getOrigin();
                Location startingPosition = getStartingPosition();

                for (BlockVector3 blockPosition : clipboard.getRegion()) {
                    BlockState blockState = clipboard.getBlock(blockPosition);

                    if (blockState.getBlockType().getMaterial().isAir()) {
                        continue;
                    }

                    int relativeX = blockPosition.x() - origin.x();
                    int relativeY = blockPosition.y() - origin.y();
                    int relativeZ = blockPosition.z() - origin.z();

                    Location location = startingPosition.clone().add(relativeX, relativeY, relativeZ);
                    Material material = BukkitAdapter.adapt(blockState.getBlockType());
                    BlockData blockData = BukkitAdapter.adapt(blockState);

                    if (material.isAir()) {
                        continue;
                    }

                    if (allBlocksNeeded.containsKey(material)) {
                        allBlocksNeeded.put(material, allBlocksNeeded.get(material) + 1);
                    } else {
                        allBlocksNeeded.put(material, 1);
                    }
                    if (material.equals(Material.AIR)) {
                        continue;
                    }
                    requiredBlocks.add(new SchemBlock(location, blockData));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
