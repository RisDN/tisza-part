package hu.ris.tiszasafeworld.listeners;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Listener;

import hu.ris.tiszasafeworld.TiszaSafeWorld;

public class CommandListener implements CommandExecutor, Listener {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!sender.hasPermission("tiszasafeworld.admin")) {
            return true;
        }

        if (args.length == 0) {
            sender.sendMessage("§cHasználat: /tiszasafeworld <setnow> <world>");
            return true;
        }

        if (args[0].equalsIgnoreCase("setnow")) {

            if (args.length != 2) {
                sender.sendMessage("§cHasználat: /tiszasafeworld <setnow> <world>");
                return true;
            }

            String worldName = args[1];
            if (sender.getServer().getWorld(worldName) == null) {
                sender.sendMessage("§cNem található ilyen világ.");
                return true;
            }

            TiszaSafeWorld.getInstance().setNow(worldName);
            TiszaSafeWorld.eraseSavedPlayers();
            TiszaSafeWorld.getInstance().reloadData();

            sender.sendMessage("§aBeállítva.");
            return true;
        }

        return true;
    }
}
