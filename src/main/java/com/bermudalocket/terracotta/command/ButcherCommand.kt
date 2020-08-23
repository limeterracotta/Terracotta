package com.bermudalocket.terracotta.command

import org.bukkit.ChatColor
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.TabExecutor
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import java.util.concurrent.atomic.AtomicInteger

object ButcherCommand: TabExecutor {

    private val flags = mutableListOf("-e", "-r", "-n")

    override fun onTabComplete(sender: CommandSender, command: Command, alias: String, args: Array<out String>): MutableList<String> {
        val currentArgIsEmptyButLastArgWasFlag = args.last() == "" && args.size >= 2 && this.flags.contains(args[args.size - 2])
        val last = if (currentArgIsEmptyButLastArgWasFlag) args[args.size - 2] else args.last()
        when (last) {
            "" -> return this.flags.filter { !args.contains(it) }.toMutableList()
            "-e" -> return EntityType.values().map { it.toString() }.toMutableList()
            "-n", "-r" -> return mutableListOf("${ChatColor.BLUE}A positive integer.")
        }
        if (last.matches(Regex("[0-9]+"))) {
            return mutableListOf()
        }
        return EntityType.values()
                .map { it.toString() }
                .filter { it.startsWith(last) }
                .toMutableList()
    }

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender is Player) {
            var type: EntityType? = null
            var radius = 0
            var limit = 0
            for (i in args.indices) {
                when {
                    args[i] == "-e" -> {
                        try {
                            type = EntityType.valueOf(args[i+1])
                        } catch (e: Exception) {
                            sender.sendMessage("${ChatColor.RED}Not a valid entity type: ${args[i+1]}")
                        }
                    }
                    args[i] == "-r" -> {
                        try {
                            radius = Integer.parseInt(args[i+1])
                        } catch (e: Exception) {
                            sender.sendMessage("${ChatColor.RED}Not a valid integer: ${args[i+1]}")
                        }
                    }
                    args[i] == "-n" -> {
                        try {
                            limit = Integer.parseInt(args[i+1])
                        } catch (e: Exception) {
                            sender.sendMessage("${ChatColor.RED}Not a valid integer: ${args[i+1]}")
                        }
                    }
                }
            }
            if (type == null || radius == 0 || limit == 0) {
                val message = when {
                    type == null -> "${ChatColor.RED}You must supply an EntityType, e.g. -e IRON_GOLEM"
                    radius == 0 -> "${ChatColor.RED}You must supply a radius as a positive integer, e.g. -r 12"
                    limit == 0 -> "${ChatColor.RED}You must supply a limit as a positive integer, e.g. -n 1"
                    else -> ""
                }
                sender.sendMessage(message)
                return true
            }

            val r = radius.toDouble()
            val entityType: EntityType = type
            val counter = AtomicInteger()
            sender.location
                    .getNearbyEntities(r, r, r)
                    .stream()
                    .filter {
                        it.type == entityType
                    }
                    .limit(limit.toLong())
                    .forEach {
                        it.remove()
                        counter.getAndIncrement()
                    }

            sender.sendMessage("${ChatColor.GRAY}Removed $counter ${type}s")
        }

        return true
    }

}
