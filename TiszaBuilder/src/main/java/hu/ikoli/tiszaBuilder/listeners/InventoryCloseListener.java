package hu.ikoli.tiszabuilder.listeners;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;

import hu.ikoli.tiszabuilder.building.Building;
import hu.ikoli.tiszabuilder.building.BuildingPlayer;
import hu.ikoli.tiszabuilder.building.ItemsAddingMenuHolder;
import hu.ikoli.tiszabuilder.config.Config;

public class InventoryCloseListener implements Listener {

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {

        if (!(event.getInventory().getHolder() instanceof ItemsAddingMenuHolder)) {
            return;
        }

        ItemStack[] items = event.getInventory().getContents();

        if (items.length == 0) {
            return;
        }

        Building building = Building.getBuildings().get(0);
        Player player = (Player) event.getPlayer();

        List<Material> alreadyCompleted = new ArrayList<>();

        for (ItemStack item : items) {
            if (item == null) {
                continue;
            }

            if (!building.isBlockNeeded(item.getType())) {
                player.sendMessage(Config.getMessage("block-not-needed").replace("%block%", item.getType().toString()));
                player.getInventory().addItem(item);
                continue;
            }

            if (building.getNextBlock(item.getType()) == null && !alreadyCompleted.contains(item.getType())) {
                alreadyCompleted.add(item.getType());
                player.getInventory().addItem(item);
                continue;
            }

            if (building.getNextBlock(item.getType()) == null) {
                continue;
            }

            BuildingPlayer buildingPlayer = BuildingPlayer.getBuildingPlayer(player);

            buildingPlayer.addPlacedBlock(item);

            building.addItem(item);

        }

        for (Material material : alreadyCompleted) {
            player.sendMessage(Config.getMessage("block-no-longer-needed").replace("%block%", material.toString()));
        }

        building.saveInventory();
        building.syncStatsToRedis();

    }

}
