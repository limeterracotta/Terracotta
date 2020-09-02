package com.bermudalocket.terracotta.command

import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player

object EntityCountCommand: CommandExecutor {

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender is Player) {
            var type: EntityType? = null
            for (i in args.indices) {
                if (args[i] == "-e") {
                    try {
                        type = EntityType.valueOf(args[i + 1])
                    } catch (e: Exception) {
                        sender.sendMessage("${ChatColor.RED}Not a valid entity type: ${args[i+1]}")
                    }
                }
            }
            if (type == null) {
                sender.sendMessage("${ChatColor.RED}You must supply an EntityType, e.g. -e IRON_GOLEM")
                return true
            }
            val entityType = type
            val count = Bukkit.getWorlds()
                    .flatMap { it.livingEntities }
                    .filter { it.type == entityType }
                    .count()
            sender.sendMessage("${ChatColor.GRAY}Found $count ${type}s.")
        }
        return true
    }

}
