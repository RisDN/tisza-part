package hu.ikoli.tiszabuilder.listeners;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDropItemEvent;
import org.bukkit.inventory.ItemStack;

public class BlockDropItemListener implements Listener {

    @EventHandler
    public void onBlockDropItem(BlockDropItemEvent event) {

        boolean hasQuartz = event.getItems().stream().anyMatch(item -> item.getItemStack().getType().equals(Material.QUARTZ));

        if (!hasQuartz) {
            return;
        }

        event.getBlock().getWorld().dropItemNaturally(event.getBlock().getLocation(), new ItemStack(Material.QUARTZ, 2));

    }

}
