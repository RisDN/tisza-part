package hu.ikoli.tiszabuilder.building;

import com.sk89q.worldedit.entity.Player;

public class BuildingPlayer {

    private Player player;
    private Building building;

    private int placedBlocks;

    public BuildingPlayer(Player player, Building building) {
        this.player = player;
        this.building = building;

    }

}
