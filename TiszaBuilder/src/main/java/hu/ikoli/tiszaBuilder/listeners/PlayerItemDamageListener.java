package hu.ikoli.tiszabuilder.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemDamageEvent;

public class PlayerItemDamageListener implements Listener {

    @EventHandler
    public void onPlayerLeave(PlayerItemDamageEvent event) {
        event.setCancelled(true);
    }

}
