package hu.ris.tiszapvparena.listeners;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import hu.ris.tiszapvparena.TiszaPvPArena;

public class CommandListener implements CommandExecutor, Listener {

    private Map<Player, Integer> cooldowns = new HashMap<>();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player)) {
            return true;
        }

        if (cooldowns.containsKey(sender)) {
            int cooldown = TiszaPvPArena.getCooldown();
            int secondsLeft = cooldowns.get(sender) + cooldown - (int) (System.currentTimeMillis() / 1000);
            if (secondsLeft > 0) {
                sender.sendMessage("§8[§fTalpra, Fiatalok§8] » §cVárnod kell még §f" + secondsLeft + " §cmásodpercet a következő teleportálásig!");
                return true;
            }

        }

        Player player = (Player) sender;
        player.teleport(TiszaPvPArena.getRandomLocation());
        player.sendMessage("§8[§fTalpra, Fiatalok§8] » §cElteleportáltál a PvP arénára! A tárgyaid halálkor itt nem vesznek el!");
        cooldowns.put(player, (int) (System.currentTimeMillis() / 1000));
        return true;
    }
}
