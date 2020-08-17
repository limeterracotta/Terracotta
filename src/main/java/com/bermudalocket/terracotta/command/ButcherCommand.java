package com.bermudalocket.terracotta.command;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.CommandPermission;
import dev.jorel.commandapi.arguments.Argument;
import dev.jorel.commandapi.arguments.EntityTypeArgument;
import dev.jorel.commandapi.arguments.IntegerArgument;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

import java.util.LinkedHashMap;

public class ButcherCommand {

    private ButcherCommand() { }

    public static void register() {
        var butcher = Bukkit.getPluginCommand("butcher");
        if (butcher != null) {
            butcher.unregister(Bukkit.getCommandMap());
        }

        var argsMap = new LinkedHashMap<String, Argument>();
        argsMap.put("entity-type", new EntityTypeArgument());
        argsMap.put("radius", new IntegerArgument(0));

        new CommandAPICommand("butcher")
            .withPermission(CommandPermission.fromString("LT.admin"))
            .withArguments(argsMap)
            .executesPlayer((player, args) -> {
                var type = (EntityType) args[0];
                var radius = (int) args[1];
                player.getNearbyEntities(radius, radius, radius)
                      .stream()
                      .filter(e -> e.getType() == type)
                      .forEach(Entity::remove);
            })
            .register();
    }

}
