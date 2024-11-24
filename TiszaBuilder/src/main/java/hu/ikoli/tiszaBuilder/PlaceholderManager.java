package hu.ikoli.tiszaBuilder;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import hu.ikoli.tiszaBuilder.config.Config;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;

public class PlaceholderManager {

    PlaceholderExpansion placeholderExpansion;

    public PlaceholderManager() {
        registerPlaceholders();
    }

    public void unregister() {
        placeholderExpansion.unregister();
    }

    private void registerPlaceholders() {
        placeholderExpansion = new PlaceholderExpansion() {
            @Override
            public @NotNull String getVersion() {
                return "1.0";
            }

            @Override
            public @NotNull String getIdentifier() {
                return "tiszabuilder";
            }

            @Override
            public @NotNull String getAuthor() {
                return "ikoliHU & RisDN";
            }

            @Override
            public String onPlaceholderRequest(Player player, @NotNull String identifier) {
                if (player == null) {
                    return null;
                }

                if (identifier.equalsIgnoreCase("prefix")) {
                    return Config.getPrefix();
                }

                return null;
            }
        };
        placeholderExpansion.register();
    }

}
