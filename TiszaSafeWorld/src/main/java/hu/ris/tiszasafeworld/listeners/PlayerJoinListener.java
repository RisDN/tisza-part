package hu.ris.tiszasafeworld.listeners;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import hu.ris.tiszasafeworld.TiszaSafeWorld;

public class PlayerJoinListener implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {

        Player player = event.getPlayer();

        long before = TiszaSafeWorld.getBefore();

        if (!player.getWorld().equals(TiszaSafeWorld.getWorld())) {
            return;
        }

        if (before < System.currentTimeMillis() && !TiszaSafeWorld.isLoggedInAfter(player)) {
            List<String> commands = TiszaSafeWorld.getInstance().getConfig().getStringList("teleport.commands");
            for (String command : commands) {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command.replace("%player%", player.getName()));
            }

            TiszaSafeWorld.setLoggedInAfter(player, true);
        }

    }
}
