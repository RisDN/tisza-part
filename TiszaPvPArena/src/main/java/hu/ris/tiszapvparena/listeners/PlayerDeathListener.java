package hu.ris.tiszapvparena.listeners;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import hu.ris.tiszapvparena.TiszaPvPArena;

public class PlayerDeathListener implements Listener {

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {

        if (!event.getPlayer().getWorld().equals(TiszaPvPArena.getWorld())) {
            return;
        }

        Player killer = event.getEntity().getKiller();
        Player victim = event.getEntity();

        for (Player player : Bukkit.getOnlinePlayers()) {
            player.sendMessage("§8[§fTalpra, Fiatalok§8] » §4" + killer.getName() + " §c" + "levadászta §4" + victim.getName() + " §cjátékost a PvP Arénában!");
        }
    }

}
