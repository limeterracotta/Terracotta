package com.bermudalocket.terracotta.command;

import com.bermudalocket.terracotta.Terracotta;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.CommandPermission;
import dev.jorel.commandapi.arguments.Argument;
import dev.jorel.commandapi.arguments.EntityTypeArgument;
import org.bukkit.Bukkit;
import org.bukkit.entity.EntityType;

import java.util.LinkedHashMap;

public class EntityCountCommand {

    private EntityCountCommand() { }

    public static void register() {
        var argsMap = new LinkedHashMap<String, Argument>();
        argsMap.put("entity-type", new EntityTypeArgument());

        new CommandAPICommand("ec")
            .withPermission(CommandPermission.fromString("LT.admin"))
            .withArguments(argsMap)
            .executesPlayer((player, args) -> {
                var type = (EntityType) args[0];
                var r = Terracotta.getViewDistance();
                var count = Bukkit.getOnlinePlayers()
                      .stream()
                      .flatMap(p -> p.getNearbyEntities(r, r, r).stream().filter(e -> e.getType() == type))
                      .count();
                player.sendMessage("[Terracotta] Found " + count + " " + type.toString());
            })
            .register();
    }

}
