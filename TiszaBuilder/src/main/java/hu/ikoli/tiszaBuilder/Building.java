package hu.ikoli.tiszaBuilder;

import java.util.ArrayList;
import java.util.List;

import hu.ikoli.tiszaBuilder.utils.FileManager;

public class Building {
    private String name;
    private String displayName;
    private boolean isBuilt;
    private FileManager buildingFile;

    public Building(String name, String displayName) {
        // Load building from file
        buildingFile = new FileManager(TiszaBuilder.getInstance(), "buildings/" + name + ".yml");
        this.name = name;
        this.displayName = displayName;
        BUILDINGS.add(this);
    }

    public String getName() {
        return name;
    }

    public String getDisplayName() {
        return displayName;
    }

    public boolean isBuilt() {
        return isBuilt;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setBuilt(boolean built) {
        isBuilt = built;
    }

    private static List<Building> BUILDINGS = new ArrayList<>();
}
