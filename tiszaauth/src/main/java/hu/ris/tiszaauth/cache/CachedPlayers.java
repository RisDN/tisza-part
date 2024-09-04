package hu.ris.tiszaauth.cache;

import java.util.ArrayList;
import java.util.List;

public class CachedPlayers {

    private static final List<String> players = new ArrayList<String>();

    public static void addPlayer(String player) {
        players.add(player);
    }

    public static void removePlayer(String player) {
        players.remove(player);
    }

    public static boolean isPlayerCached(String player) {
        return players.contains(player);
    }

    public static List<String> getPlayers() {
        return players;
    }

}
