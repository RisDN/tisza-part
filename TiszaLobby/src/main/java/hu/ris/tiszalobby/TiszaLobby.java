package hu.ris.tiszalobby;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

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
        for (Player p : getServer().getOnlinePlayers()) {
            if (p.getPlayer() == null) {
                continue;
            }
            p.getPlayer().hidePlayer(this, player);
            player.hidePlayer(this, p);
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        Player player = (Player) event.getPlayer();
        runCommands(player);
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