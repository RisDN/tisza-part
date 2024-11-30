package hu.ikoli.tiszabuilder.listeners;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import hu.ikoli.tiszabuilder.building.Building;
import hu.ikoli.tiszabuilder.building.ItemAdding;
import hu.ikoli.tiszabuilder.config.Config;
import hu.ikoli.tiszabuilder.utils.Utils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent.Builder;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.ClickEvent.Action;

public class CommandListener implements CommandExecutor, Listener, TabCompleter {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            return true;
        }

        if (args.length == 0) {
            return true;
        }

        if (args[0].equalsIgnoreCase("additems") && Config.isBuildingServer()) {
            ItemAdding.openItemAddingMenu(player);
            return true;
        }

        if (args[0].equalsIgnoreCase("items")) {
            if (Building.getBuildings().isEmpty()) {
                player.sendMessage(Config.getMessage("no-buildings"));
                return true;
            }
            Building building = Building.getBuildings().get(0);
            String pageString = args.length == 1 ? "1" : args[1];
            int pages = (int) Math.ceil(((double) building.getBlocksNeeded().size() / 10));
            if (!Utils.isPositiveInteger(pageString) || Integer.parseInt(pageString) > pages) {
                player.sendMessage(Config.getMessage("not-accesible-page"));
                return true;
            }
            int page = Integer.parseInt(pageString);
            // A blocksNeeded map-et rendezzük az értékek szerint csökkenő sorrendbe
            Map<Material, Integer> blocksNeeded = building.getBlocksNeeded().entrySet().stream().sorted(Entry.<Material, Integer>comparingByValue().reversed()).skip((page - 1) * 10).limit(10) // Csökkenő
                                                                                                                                                                                                // sorrend
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, // Ütközéskezelés, ha lenne ismétlődő kulcs
                            LinkedHashMap::new // Sorrendet megőrző Map
                    ));

            player.sendMessage("");
            player.sendMessage("§aAz épülethez szükséges blokkok:");
            for (Entry<Material, Integer> entry : blocksNeeded.entrySet()) {
                player.sendMessage("§7" + entry.getKey().name() + " §8- §e" + entry.getValue());
            }

            Component message;
            Component back = Component.text().content(Config.getMessage("items-list.back"))
                    .clickEvent(ClickEvent.clickEvent(Action.RUN_COMMAND, "/%command% items ".replace("%command%", label) + (page - 1))).build();
            Component next = Component.text().content(Config.getMessage("items-list.next"))
                    .clickEvent(ClickEvent.clickEvent(Action.RUN_COMMAND, "/%command% items ".replace("%command%", label) + (page + 1))).build();
            Builder pagesComponent = Component.text();
            for (int i = 0; i < pages; i++) {
                if (i + 1 == page) {
                    pagesComponent.append(Component.text().content(Config.getMessage("items-list.current-page").replace("%page%", String.valueOf(i + 1))));
                    continue;
                }
                pagesComponent.append(Component.text().content(Config.getMessage("items-list.page").replace("%page%", String.valueOf(i + 1)))
                        .clickEvent(ClickEvent.clickEvent(Action.RUN_COMMAND, "/%command% items ".replace("%command%", label) + (i + 1))));
            }

            if (page == 1) {
                message = Component.text().append(pagesComponent.build()).append(next).build();
            } else if (page < pages) {
                message = Component.text().append(back).append(pagesComponent.build()).append(next).build();
            } else {
                message = Component.text().append(back).append(pagesComponent.build()).build();
            }
            player.sendMessage(message);

            return true;
        }

        if (player.hasPermission("tiszabuilder.admin")) {
            if (args.length == 0) {
                // replacel teszem be a parancsot, így jobban átlátható
                player.sendMessage("§cHasználat: /%command% <reload | show>".replace("%command%", label));
                return true;
            }

            if (args[0].equalsIgnoreCase("servertype")) {
                player.sendMessage("§aA szerver típusa: " + Config.getServerType().name());
                return true;
            }

            if (args[0].equalsIgnoreCase("reload")) {
                Config.reloadConfig();
                player.sendMessage(Config.getMessage("reloaded"));
                return true;
            }

            if (args[0].equalsIgnoreCase("show")) {
                if (args.length != 2) {
                    player.sendMessage("§cHasználat: /%command% show <fájlnév>".replace("%command%", label));
                    return true;
                }

                Building building = Building.getBuilding(args[1]);
                building.displayParticlesOnBlocks();
            }
            return true;
        } else {
            player.sendMessage("§cHasználat: /%command% additems".replace("%command%", label));
            return true;
        }
    }

    private final List<String> commands = Arrays.asList("additems", "items", "servertype", "reload", "show");

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String lable, String[] args) {
        if (sender.hasPermission("tiszabuilder.admin")) {
            if (args.length == 1) {
                return commands;
            }

            if (args.length == 2) {
                if (args[0].equalsIgnoreCase("show")) {
                    List<String> buildings = Building.getBuildingNames();
                    if (buildings.isEmpty()) {
                        return null;
                    }
                    Collections.sort(buildings);
                    return buildings;
                }
            }

            return null;
        }
        return Arrays.asList("additems", "items");
    }
}
