package hu.ikoli.tiszabuilder.utils;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import hu.ikoli.tiszabuilder.config.Config;
import net.md_5.bungee.api.ChatColor;

public class Utils {

    private static final Pattern pattern = Pattern.compile("#[a-fA-F0-9]{6}");

    public static boolean hasSpace(Player player) {
        return player.getInventory().firstEmpty() != -1;
    }

    public static int getFinalAmountAfterFees(int amount) {
        double feePercent = Config.getDouble("settings.fyrecoin-fee-percent");
        return (int) Math.round(amount * (1 + (feePercent / 100)));
    }

    public static List<String> color(List<String> messages) {
        for (int i = 0; i < messages.size(); i++) {
            messages.set(i, color(messages.get(i)));
        }
        return messages;
    }

    public static String color(String message) {
        Matcher match = pattern.matcher(message);
        while (match.find()) {
            if (message.charAt(match.start() - 1) == '&') {
                String color = message.substring(match.start(), match.end());
                message = message.replace("&" + color, ChatColor.of(color) + "");
                match = pattern.matcher(message);
            }
        }
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    public static void error(String message) {
        Bukkit.getLogger().severe(message);
    }

    public static void warn(String message) {
        Bukkit.getLogger().warning(message);
    }

    public static void warn(Integer message) {
        warn(message.toString());
    }

    public static void warn(Double message) {
        warn(message.toString());
    }

    public static void warn(Float message) {
        warn(message.toString());
    }

    public static void warn(Boolean message) {
        warn(message.toString());
    }

    public static void log(String message) {
        Bukkit.getLogger().info(message);
    }

    public static void log(Long message) {
        log(message.toString());
    }

    public static void log(Integer message) {
        log(message.toString());
    }

    public static void log(Double message) {
        log(message.toString());
    }

    public static void log(Float message) {
        log(message.toString());
    }

    public static void log(Boolean message) {
        log(message.toString());
    }

}
