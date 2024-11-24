package hu.ikoli.tiszabuilder.listeners;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import hu.ikoli.tiszabuilder.building.Building;
import hu.ikoli.tiszabuilder.config.Config;

public class CommandListener implements CommandExecutor, Listener {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            return true;
        }
        if (player.hasPermission("tiszabuilder.admin")) {
            if (args.length == 0) {
                player.sendMessage("§cUsage: /tiszabuilder <reload | show>");
                return true;
            }

            if (args[0].equalsIgnoreCase("reload")) {
                Config.reloadConfig();
                player.sendMessage(Config.getMessage("reloaded"));
                return true;
            }

            if (args[0].equalsIgnoreCase("show")) {
                if (args.length != 2) {
                    player.sendMessage("§cUsage: /tiszabuilder show <fájlnév>");
                    return true;
                }

                Building building = Building.getBuilding(args[1]);
                building.displayParticlesOnBlocks();
            }
            return true;
        }

        return true;
    }
}
