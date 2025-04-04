package hu.ikoli.tiszabuilder.utils;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.md_5.bungee.api.ChatColor;

public class Utils {

    private static final Pattern pattern = Pattern.compile("#[a-fA-F0-9]{6}");

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

    public static boolean isPositiveInteger(String str) {
        return str != null && str.matches("[1-9][0-9]*"); // regex for natural numbers (positive integers)
    }

    public static double round(double value, int places) {
        if (places < 0)
            throw new IllegalArgumentException();

        double factor = (long) Math.pow(10, places);
        value = value * factor;
        double tmp = Math.round(value);
        return tmp / factor;
    }

}
