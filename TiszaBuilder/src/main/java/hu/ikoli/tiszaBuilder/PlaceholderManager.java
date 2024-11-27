package hu.ikoli.tiszabuilder;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import hu.ikoli.tiszabuilder.building.Building;
import hu.ikoli.tiszabuilder.building.BuildingPlayer;
import hu.ikoli.tiszabuilder.building.PlayerStats;
import hu.ikoli.tiszabuilder.config.Config;
import hu.ikoli.tiszabuilder.config.ServerType;
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

                boolean isBuildingServer = Config.getServerType().equals(ServerType.BUILDING);

                if (!isBuildingServer) {

                    BuildingPlayer buildingPlayer = BuildingPlayer.getBuildingPlayer(player);
                    PlayerStats stats = buildingPlayer.getPlayerStats();

                    if (stats == null) {
                        return "???";
                    }

                    if (identifier.equalsIgnoreCase("building_displayname")) {
                        return stats.getBuilding_displayname();
                    }

                    if (identifier.equalsIgnoreCase("building_filename")) {
                        return stats.getBuilding_filename();
                    }

                    if (identifier.equalsIgnoreCase("building_progress")) {
                        return String.valueOf(Utils.round(stats.getBuilding_progress(), 2));
                    }

                    if (identifier.equalsIgnoreCase("building_blocks_needed")) {
                        return String.valueOf(stats.getBuilding_blocks_needed());
                    }

                    if (identifier.equalsIgnoreCase("building_blocks_placed")) {
                        return String.valueOf(stats.getBuilding_blocks_placed());
                    }

                    if (identifier.equalsIgnoreCase("building_contributors")) {
                        return String.valueOf(stats.getBuilding_contributors());
                    }

                    if (identifier.equalsIgnoreCase("player_blocks_placed")) {
                        return String.valueOf(stats.getPlayer_blocks_placed());
                    }

                    if (identifier.equalsIgnoreCase("player_blocks_placed_progress")) {
                        return String.valueOf(Utils.round(stats.getPlayer_blocks_placed_progress(), 2));
                    }

                    if (identifier.equalsIgnoreCase("player_contrubution_place")) {
                        return String.valueOf(stats.getPlayer_contrubution_place());
                    }

                    return "-";
                }

                if (isBuildingServer) {
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
                }

                return "-";
            }
        };
        placeholderExpansion.register();
    }

}
