package hu.ris.tiszaautocreative;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class TiszaAutoCreative extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {

        getServer().getPluginManager().registerEvents(this, this);
        saveDefaultConfig();

        getLogger().info("TiszaAutoCreative started.");

    }

    @Override
    public void onDisable() {
        getLogger().info("TiszaAutoCreative stopped.");
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Bukkit.getScheduler().runTaskLater(this, new Runnable() {
            @Override
            public void run() {
                if (!event.getPlayer().getGameMode().equals(GameMode.CREATIVE)) {
                    event.getPlayer().setGameMode(GameMode.CREATIVE);
                }
            }
        }, 10);
    }

}