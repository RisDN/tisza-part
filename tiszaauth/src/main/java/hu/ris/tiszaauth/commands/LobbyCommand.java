package hu.ris.tiszaauth.commands;

import java.util.List;

import com.velocitypowered.api.command.CommandManager;
import com.velocitypowered.api.command.CommandMeta.Builder;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.server.RegisteredServer;

import hu.ris.tiszaauth.TiszaAuth;
import hu.ris.tiszaauth.config.Config;

public class LobbyCommand implements SimpleCommand {

    @Override
    public void execute(Invocation invocation) {
        Player player = (Player) invocation.source();
        String[] args = invocation.arguments();

        if (args.length == 1 && args[0].equalsIgnoreCase("reload") && player.hasPermission("tiszaauth.reload")) {
            Config.reloadConfig();
            unregister();
            register();
            player.sendMessage(Config.getMessage("reload"));
            return;
        }

    }

    public static void register() {
        if (!Config.getBoolean("lobby.enable")) {
            return;
        }
        List<String> commands = Config.getStringList("lobby.commands");
        if (commands.isEmpty()) {
            return;
        }
        Builder meta;
        CommandManager commandManager = TiszaAuth.getServer().getCommandManager();
        if (commands.size() == 1) {
            meta = commandManager.metaBuilder(commands.get(0));
        } else {
            meta = commandManager.metaBuilder(commands.get(0));
            for (String command : commands) {
                meta.aliases(command);
            }
        }
        commandManager.register(meta.build(), new LobbyCommand());
    }

    public static void unregister() {
        if (!Config.getBoolean("lobby.enable")) {
            return;
        }
        List<String> commands = Config.getStringList("lobby.commands");
        if (commands.isEmpty()) {
            return;
        }
        CommandManager commandManager = TiszaAuth.getServer().getCommandManager();
        for (String command : commands) {
            commandManager.unregister(command);
        }
    }

}
