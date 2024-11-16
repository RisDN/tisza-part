package hu.ris.tiszaauth.listeners;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.player.ServerPreConnectEvent;

import hu.ris.tiszaauth.TiszaAuth;
import hu.ris.tiszaauth.cache.CachedPlayers;
import hu.ris.tiszaauth.config.Config;
import net.kyori.adventure.text.minimessage.MiniMessage;

public class PlayerJoinListener {

    private static final TiszaAuth plugin = TiszaAuth.getInstance();

    @Subscribe
    public void onPlayerConnect(ServerPreConnectEvent event) {
        String playerName = event.getPlayer().getUsername();

        if (CachedPlayers.isPlayerCached(playerName)) {
            return;
        }

        TiszaAuth.getServer().getScheduler().buildTask(plugin, () -> {
            if (TiszaAuth.isPlayerLinked(playerName)) {
                CachedPlayers.addPlayer(playerName);
                return;
            }
            TiszaAuth.getServer().getScheduler().buildTask(plugin, () -> {
                event.getPlayer().disconnect(MiniMessage.miniMessage().deserialize(Config.getString("messages.not_linked").replaceAll("§", "").replaceAll("&", "")));
                event.setResult(ServerPreConnectEvent.ServerResult.denied());
            }).schedule();

        }).schedule();

    }

}
