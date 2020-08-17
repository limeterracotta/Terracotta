package com.bermudalocket.terracotta.command;

import com.bermudalocket.terracotta.Terracotta;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

public class EntityCountCommand implements CommandExecutor {

    public static final EntityCountCommand INSTANCE = new EntityCountCommand();

    private EntityCountCommand() { }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player player) {
            EntityType type = null;
            for (int i = 0; i < args.length - 1; i++) {
                if (args[i].equalsIgnoreCase("-e")) {
                    try {
                        type = EntityType.valueOf(args[i+1]);
                    } catch (Exception e) {
                        player.sendMessage(ChatColor.RED + "Not a valid entity type: " + args[i+1]);
                    }
                }
            }
            if (type == null) {
                player.sendMessage(ChatColor.RED + "You must supply an EntityType, e.g. -e IRON_GOLEM.");
                return true;
            }

            final var entityType = type;
            var r = Terracotta.getViewDistance();
            var count = Bukkit.getOnlinePlayers()
                              .stream()
                              .flatMap(p -> p.getNearbyEntities(r, r, r).stream().filter(e -> e.getType() == entityType))
                              .count();
            player.sendMessage(ChatColor.GRAY + "Found " + count + " " + type.toString() + "s.");

        }
        return true;
    }

}
