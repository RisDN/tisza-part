package hu.ikoli.tiszabuilder.building;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import net.kyori.adventure.text.Component;

public class ItemAdding {

    public static void openItemAddingMenu(Player player) {

        Inventory inventory = Bukkit.getServer().createInventory(new ItemsAddingMenuHolder(), 54, Component.text("Tárgy hozzáadása"));

        player.openInventory(inventory);

    }

}
