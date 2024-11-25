package hu.ikoli.tiszabuilder;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import hu.ikoli.tiszabuilder.building.Building;
import hu.ikoli.tiszabuilder.building.BuildingPlayer;
import hu.ikoli.tiszabuilder.config.Config;
import hu.ikoli.tiszabuilder.utils.Utils;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;

public class PlaceholderManager {

    PlaceholderExpansion placeholderExpansion;

    public PlaceholderManager() {
        registerPlaceholders();
    }

    public void unregister() {
        placeholderExpansion.unregister();
    }

    private void registerPlaceholders() {
        placeholderExpansion = new PlaceholderExpansion() {
            @Override
            public @NotNull String getVersion() {
                return "1.0";
            }

            @Override
            public @NotNull String getIdentifier() {
                return "tiszabuilder";
            }

            @Override
            public @NotNull String getAuthor() {
                return "ikoliHU & Ris";
            }

            @Override
            public String onPlaceholderRequest(Player player, @NotNull String identifier) {
                if (player == null) {
                    return null;
                }

                if (identifier.equalsIgnoreCase("prefix")) {
                    return Config.getPrefix();
                }

                Building building = Building.getBuildings().get(0);

                if (building == null) {
                    return "???";
                }

                BuildingPlayer buildingPlayer = BuildingPlayer.getBuildingPlayer(player);

                if (identifier.equalsIgnoreCase("building_displayname")) {
                    return building.getDisplayname();
                }

                if (identifier.equalsIgnoreCase("building_filename")) {
                    return building.getFileName();
                }

                if (identifier.equalsIgnoreCase("building_progress")) {
                    return String.valueOf(Utils.round(building.getProgress(), 2));
                }

                if (identifier.equalsIgnoreCase("building_blocks_needed")) {
                    return String.valueOf(building.getAllBlocksRequiredCount());
                }

                if (identifier.equalsIgnoreCase("building_blocks_placed")) {
                    return String.valueOf(building.getPlacedBlocksCount());
                }

                if (identifier.equalsIgnoreCase("building_contributors")) {
                    return String.valueOf(BuildingPlayer.getContributorsCount());
                }

                if (identifier.equalsIgnoreCase("player_blocks_placed")) {
                    return String.valueOf(buildingPlayer.getBlocksPlaced());
                }

                if (identifier.equalsIgnoreCase("player_blocks_placed_progress")) {
                    return String.valueOf(Utils.round(building.getProgress(buildingPlayer), 2));
                }

                if (identifier.equalsIgnoreCase("player_contrubution_place")) {
                    return String.valueOf(BuildingPlayer.getContrubotorPlace(player.getName()));
                }

                return "-";
            }
        };
        placeholderExpansion.register();
    }

}
