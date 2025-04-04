package hu.ris.tisszacrosschat;

import org.bukkit.plugin.java.JavaPlugin;

import hu.ris.tisszacrosschat.listeners.ChatMessageListener;
import hu.ris.tisszacrosschat.services.SocketIOClient;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.ClickEvent.Action;
import utils.Utils;

public class TiszaCrossChat extends JavaPlugin {

    private static TiszaCrossChat instance;

    @Override
    public void onEnable() {
        instance = this;

        saveDefaultConfig();

        getServer().getPluginManager().registerEvents(new ChatMessageListener(), this);
        getLogger().info("TiszaCrossChat started.");

        SocketIOClient.init();
    }

    @Override
    public void onDisable() {
        getLogger().info("TiszaCrossChat stopped.");
        SocketIOClient.disconnect();
    }

    public static TiszaCrossChat getInstance() {
        return instance;
    }

    public static void broadCastMessage(String message, String sender, String messageUrl) {
        String formattedMessage = getInstance().getConfig().getString("message-format").replace("%message%", message).replace("%player%", sender);
        int splitThreshold = getInstance().getConfig().getInt("split-threshold", 400);
        if (formattedMessage.length() > splitThreshold) {
            formattedMessage = formattedMessage.substring(0, splitThreshold) + "... ";
        }

        String mess = Utils.color(formattedMessage);

        getInstance().getServer().getOnlinePlayers().forEach(player -> player.sendMessage(Component.text().content(mess).clickEvent(ClickEvent.clickEvent(Action.OPEN_URL, messageUrl))));
    }

}