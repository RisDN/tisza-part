package hu.ris.tisszacrosschat.services;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import java.net.URISyntaxException;

import hu.ris.tisszacrosschat.TiszaCrossChat;

public class SocketIOClient {

    public static Socket socket;

    public static void init() {
        try {

            String url = TiszaCrossChat.getInstance().getConfig().getString("socketserver.address");
            String port = TiszaCrossChat.getInstance().getConfig().getString("socketserver.port");

            socket = IO.socket("http://" + url + ":" + port); // Cseréld le a megfelelő URL-re

            loadListeners();

            socket.connect();

        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    public static Socket getSocket() {
        return socket;
    }

    public static void disconnect() {
        socket.disconnect();
    }

    public static void sendChatMessage(String message, String sender) {

        if (!socket.connected()) {
            System.out.println("Nincs kapcsolat a socket.io szerverrel!");
            return;
        }

        socket.emit("chat", message, sender);
    }

    private static void loadListeners() {

        socket.on("chat", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                System.out.println("Üzenet érkezett: " + args[0] + " - " + args[1]);
                TiszaCrossChat.broadCastMessage((String) args[0], (String) args[1], (String) args[2]);
            }
        });

        socket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                System.out.println("Csatlakozás sikeres!");
            }
        });

        socket.on(Socket.EVENT_CONNECT_ERROR, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                System.err.println("Kapcsolódási hiba: " + args[0]);
            }
        });
    }
}
