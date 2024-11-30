package hu.ris.tiszaauth;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;

public class Utils {
    private static final Pattern pattern = Pattern.compile("#[a-fA-F0-9]{6}");
    private static final Pattern oldPattern = Pattern.compile("&[a-fA-F0-9lkLKmMnNrRoO]");

    public static final Pattern HEX_PATTERN = Pattern.compile("&#(\\w{5}[0-9A-Fa-f])", Pattern.CASE_INSENSITIVE);
    private static final Map<String, String> colorCodes = new HashMap<>() {
        {
            put("&0", "<black>");
            put("&1", "<dark_blue>");
            put("&2", "<dark_green>");
            put("&3", "<dark_aqua>");
            put("&4", "<dark_red>");
            put("&5", "<dark_purple>");
            put("&6", "<gold>");
            put("&7", "<gray>");
            put("&8", "<dark_gray>");
            put("&9", "<blue>");
            put("&a", "<green>");
            put("&b", "<aqua>");
            put("&c", "<red>");
            put("&d", "<light_purple>");
            put("&e", "<yellow>");
            put("&f", "<white>");
            put("&k", "<obfuscated>");
            put("&l", "<bold>");
            put("&m", "<strikethrough>");
            put("&n", "<underlined>");
            put("&o", "<italic>");
            put("&r", "<reset>");
        }
    };

    public static Component color(String message) {
        if (message == null) {
            return null;
        }
        Matcher match = pattern.matcher(message);
        while (match.find()) {
            int charPos = match.start() - 1;
            if (charPos >= 0 && message.charAt(charPos) == '&') {
                String color = message.substring(match.start(), match.end());
                message = message.replace("&" + color, "<" + color + ">");
                match = pattern.matcher(message);
            }
        }
        Matcher matchOld = oldPattern.matcher(message);
        while (matchOld.find()) {
            String color = message.substring(matchOld.start(), matchOld.end());
            message = message.replace(color, colorCodes.get(color));
            matchOld = oldPattern.matcher(message);
        }
        return toComponent("<!i>" + message);
    }

    public static Component toComponent(String message) {
        return MiniMessage.miniMessage().deserialize(message);
    }

    public static String fromComponent(Component component) {
        return MiniMessage.miniMessage().serialize(component);
    }
}
