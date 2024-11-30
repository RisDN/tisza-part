package hu.ikoli.tiszabuilder.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import hu.ikoli.tiszabuilder.building.BuildingPlayer;
import hu.ikoli.tiszabuilder.config.Config;
import hu.ikoli.tiszabuilder.config.ServerType;

public class PlayerJoinListener implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        BuildingPlayer.getBuildingPlayer(event.getPlayer());

        if (Config.getServerType().equals(ServerType.BUILDING)) {
            event.getPlayer().setAllowFlight(true);
            event.getPlayer().setFlying(true);
        }

    }

}
