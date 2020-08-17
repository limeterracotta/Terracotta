package com.bermudalocket.terracotta.command;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.util.concurrent.atomic.AtomicInteger;

public class ButcherCommand implements CommandExecutor {

    public static final ButcherCommand INSTANCE = new ButcherCommand();

    private ButcherCommand() { }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player player) {
            EntityType type = null;
            int radius = 0;
            int limit = 0;
            for (int i = 0; i < args.length; i++) {
                if (args[i].equalsIgnoreCase("-e")) {
                    try {
                        type = EntityType.valueOf(args[i+1]);
                    } catch (Exception e) {
                        player.sendMessage(ChatColor.RED + "Not a valid entity type: " + args[i+1]);
                    }
                } else if (args[i].equalsIgnoreCase("-r")) {
                    try {
                        radius = Integer.parseInt(args[i+1]);
                    } catch (Exception e) {
                        player.sendMessage(ChatColor.RED + "Not a valid integer: " + args[i+1]);
                    }
                } else if (args[i].equalsIgnoreCase("-n")) {
                    try {
                        limit = Integer.parseInt(args[i+1]);
                    } catch (Exception e) {
                        player.sendMessage(ChatColor.RED + "Not a valid integer: " + args[i+1]);
                    }
                }
            }
            if (type == null) {
                player.sendMessage(ChatColor.RED + "You must supply an EntityType, e.g. -e IRON_GOLEM.");
                return true;
            }
            if (radius == 0) {
                player.sendMessage(ChatColor.RED + "You must supply a radius as an integer, e.g. -r 10.");
                return true;
            }
            if (limit == 0) {
                player.sendMessage(ChatColor.RED + "You must supply a limit as an integer, e.g. -n 5.");
                return true;
            }

            final EntityType entityType = type;
            AtomicInteger counter = new AtomicInteger();
            player.getLocation()
                  .getNearbyEntities(radius, radius, radius)
                  .stream()
                  .filter(e -> e.getType() == entityType)
                  .limit(limit)
                  .forEach(e -> {
                      e.remove();
                      counter.getAndIncrement();
                  });

            player.sendMessage(ChatColor.GRAY + "[Terracotta] Removed " + counter + " " + type.toString() + "s.");
        }
        return true;
    }
}
