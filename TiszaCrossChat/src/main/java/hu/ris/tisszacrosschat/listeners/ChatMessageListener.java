package hu.ris.tisszacrosschat.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import hu.ris.tisszacrosschat.services.SocketIOClient;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;

public class ChatMessageListener implements Listener {

    @EventHandler
    public void onChatMessage(AsyncChatEvent event) {

        String message = PlainTextComponentSerializer.plainText().serialize(event.message());
        String sender = event.getPlayer().getName();

        SocketIOClient.sendChatMessage(message, sender);
    }

}
