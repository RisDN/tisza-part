package hu.ris.tiszalobby;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import fr.xephi.authme.events.LoginEvent;

public class TiszaLobby extends JavaPlugin implements Listener {

    private static TiszaLobby instance;

    @Override
    public void onEnable() {
        instance = this;

        getServer().getPluginManager().registerEvents(this, this);
        saveDefaultConfig();

        getLogger().info("TiszaLobby started.");

    }

    @Override
    public void onDisable() {
        getLogger().info("TiszaLobby stopped.");
    }

    @EventHandler
    public void onLoggedIn(LoginEvent event) {
        Player player = event.getPlayer();
        runCommands(player);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        for (Player ply : getServer().getOnlinePlayers()) {
            if (ply.getPlayer() == null) {
                continue;
            }
            if (!player.hasPermission("tiszalobby.bypass")) {
                player.hidePlayer(this, ply);
            }
            if (!ply.hasPermission("tiszalobby.bypass")) {
                ply.hidePlayer(this, player);
            }
        }
        if (!player.hasPermission("tiszalobby.bypass")) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, -1, 1, false, false, false));
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        Player player = (Player) event.getPlayer();
        Bukkit.getScheduler().runTaskLater(instance, new Runnable() {
            @Override
            public void run() {
                runCommands(player);
            }
        }, 30 * 20);
    }

    private void runCommands(Player player) {

        if (player.hasPermission("tiszalobby.bypass")) {
            return;
        }

        getConfig().getStringList("commands-run-after-login").forEach(command -> {
            getServer().dispatchCommand(getServer().getConsoleSender(), command.replace("%player%", player.getName()));
        });
    }

    public static TiszaLobby getInstance() {
        return instance;
    }

}