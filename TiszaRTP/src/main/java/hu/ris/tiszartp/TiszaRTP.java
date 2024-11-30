package hu.ris.tiszartp;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.plugin.java.JavaPlugin;

import hu.ris.tiszartp.listeners.CommandListener;
import net.md_5.bungee.api.ChatColor;

public class TiszaRTP extends JavaPlugin {

    private static TiszaRTP instance;

    @Override
    public void onEnable() {
        instance = this;

        CommandListener commandListener = new CommandListener();
        getCommand("rtp").setExecutor(commandListener);

        getLogger().info("TiszaRTP plugin has been enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info("TiszaRTP plugin has been disabled!");
    }

    public static TiszaRTP getInstance() {
        return instance;
    }

    public static String getWorldType() {
        return getInstance().getConfig().getString("modules.randomtp.world-type");
    }

    public static boolean isNether() {
        return getWorldType().equalsIgnoreCase("nether");
    }

    public static String getMessage(String path) {
        String prefix = color(getInstance().getConfig().getString("prefix"));
        return color(getInstance().getConfig().getString("messages." + path).replace("%prefix%", prefix));
    }

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

}