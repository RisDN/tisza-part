package hu.ikoli.tiszabuilder.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import hu.ikoli.tiszabuilder.building.BuildingPlayer;

public class PlayerJoinListener implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        BuildingPlayer.getBuildingPlayer(event.getPlayer());
    }

}
