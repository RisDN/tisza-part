package hu.ikoli.tiszabuilder.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;

import hu.ikoli.tiszabuilder.building.Building;
import hu.ikoli.tiszabuilder.building.BuildingPlayer;
import hu.ikoli.tiszabuilder.building.ItemsAddingMenuHolder;

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

        for (ItemStack item : items) {
            if (item == null) {
                continue;
            }

            if (!building.isBlockNeeded(item.getType())) {
                player.sendMessage("This block is not needed for this building!" + item.getType());
                player.getInventory().addItem(item);
                continue;
            }

            BuildingPlayer buildingPlayer = BuildingPlayer.getBuildingPlayer(player);

            buildingPlayer.addPlacedBlock(item);

            building.addItem(item);

        }

    }

}
