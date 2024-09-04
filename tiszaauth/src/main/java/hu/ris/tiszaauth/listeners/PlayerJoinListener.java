package hu.ris.tiszaauth.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent.Result;

import hu.ris.tiszaauth.TiszaAuth;
import hu.ris.tiszaauth.cache.CachedPlayers;

public class PlayerJoinListener implements Listener {

    private static final TiszaAuth plugin = TiszaAuth.getInstance();

    @EventHandler
    public void onPlayerJoin(AsyncPlayerPreLoginEvent event) {
        String playerName = event.getName();

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

                event.disallow(Result.KICK_OTHER, "§cAmíg nem csatlakoztattad össze a Discord fiókodat a karaktereddel, addig nem tudsz belépni a szerverre!");
            }
        });

    }

}
