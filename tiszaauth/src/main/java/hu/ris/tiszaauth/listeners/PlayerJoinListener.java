package hu.ris.tiszaauth.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent.Result;

import hu.ris.tiszaauth.TiszaAuth;
import hu.ris.tiszaauth.cache.CachedPlayers;
import net.kyori.adventure.text.Component;

public class PlayerJoinListener implements Listener {

    private static final TiszaAuth plugin = TiszaAuth.getInstance();

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        String playerName = event.getPlayer().getName();

        if (CachedPlayers.isPlayerCached(playerName)) {
            return;
        }

        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, new Runnable() {
            @Override
            public void run() {
                if (TiszaAuth.isPlayerLinked(playerName)) {

                    CachedPlayers.addPlayer(playerName);
                    return;
                }

                plugin.getServer().getScheduler().runTask(plugin, new Runnable() {
                    @Override
                    public void run() {
                        event.getPlayer().kick(Component.text("§cAmíg nem csatlakoztattad össze a Discord fiókodat a karaktereddel, addig nem tudsz belépni a szerverre!"));
                    }
                });
            };

        });

    }

}
