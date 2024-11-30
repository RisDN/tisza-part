package hu.ikoli.tiszabuilder.building;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import hu.ikoli.tiszabuilder.config.Config;
import net.kyori.adventure.text.Component;

public class ItemAdding {

    public static void openItemAddingMenu(Player player) {

        Inventory inventory = Bukkit.getServer().createInventory(new ItemsAddingMenuHolder(), 54, Component.text(Config.getString("messages.item-adding-menu-title")));

        player.openInventory(inventory);

    }

}
