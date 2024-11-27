package hu.ikoli.tiszabuilder.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import hu.ikoli.tiszabuilder.building.BuildingPlayer;

public class PlayerQuitListener implements Listener {

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event) {
        BuildingPlayer player = BuildingPlayer.getBuildingPlayer(event.getPlayer());

        player.save();
    }

}
