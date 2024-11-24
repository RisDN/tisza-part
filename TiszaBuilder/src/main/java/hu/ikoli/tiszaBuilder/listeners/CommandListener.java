package hu.ikoli.tiszaBuilder.listeners;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import hu.ikoli.tiszaBuilder.config.Config;

public class CommandListener implements CommandExecutor, Listener {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            return true;
        }
        if (player.hasPermission("tiszabuilder.admin")) {
            Config.reloadConfig();
            player.sendMessage(Config.getMessage("reloaded"));
            return true;
        }

        return true;
    }
}
