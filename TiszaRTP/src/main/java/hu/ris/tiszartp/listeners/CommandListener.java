package hu.ris.tiszartp.listeners;

import java.util.HashMap;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import hu.ris.tiszartp.RandomTP;
import hu.ris.tiszartp.TiszaRTP;

public class CommandListener implements CommandExecutor, Listener {

    private final HashMap<Player, Long> cooldowns = new HashMap<>();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player player)) {
            return true;
        }

        long cooldownTime = TiszaRTP.getInstance().getConfig().getLong("modules.randomtp.cooldown-seconds") * 1000L;

        if (this.cooldowns.containsKey(player) && System.currentTimeMillis() - ((Long) this.cooldowns.get(player)).longValue() < cooldownTime) {
            long remainingTime = (cooldownTime - System.currentTimeMillis() - ((Long) this.cooldowns.get(player)).longValue()) / 1000L;
            player.sendMessage(TiszaRTP.getMessage("rtp.cooldown").replace("%cooldown%", String.valueOf(remainingTime)));

            return true;
        }
        this.cooldowns.put(player, Long.valueOf(System.currentTimeMillis()));
        player.sendMessage(TiszaRTP.getMessage("rtp.place-finding"));
        new RandomTP(player);
        return true;
    }
}
