package hu.ris.tiszaauth.listeners;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.PostLoginEvent;
import com.velocitypowered.api.proxy.Player;

import hu.ris.tiszaauth.TiszaAuth;
import hu.ris.tiszaauth.config.Config;
import net.kyori.adventure.text.minimessage.MiniMessage;

public class ProxyConnectListener {

    private static final TiszaAuth plugin = TiszaAuth.getInstance();

    @Subscribe
    public void onProxyConnect(PostLoginEvent event) {
        Player player = event.getPlayer();
        String playerName = event.getPlayer().getUsername();

        TiszaAuth.getServer().getScheduler().buildTask(plugin, () -> {

            String savedIp = TiszaAuth.getSavedIp(playerName);
            boolean isSet = savedIp != null && savedIp != "";

            if (!isSet) {
                TiszaAuth.getServer().getScheduler().buildTask(plugin, () -> {
                    event.getPlayer().disconnect(MiniMessage.miniMessage().deserialize(Config.getString("messages.twofa-not-set").replaceAll("§", "").replaceAll("&", "")));
                }).schedule();
                return;
            }

            if (isSet && !savedIp.equals(player.getRemoteAddress().getAddress().getHostAddress())) {
                TiszaAuth.getServer().getScheduler().buildTask(plugin, () -> {
                    event.getPlayer().disconnect(MiniMessage.miniMessage().deserialize(Config.getString("messages.twofa-not-matching").replaceAll("§", "").replaceAll("&", "")));
                }).schedule();
                return;
            }

        }).schedule();

    }

}
