package com.bermudalocket.terracotta

import br.com.devsrsouza.kotlinbukkitapi.architecture.KotlinPlugin
import br.com.devsrsouza.kotlinbukkitapi.extensions.event.event
import br.com.devsrsouza.kotlinbukkitapi.extensions.event.events
import com.bermudalocket.terracotta.command.ButcherCommand
import com.bermudalocket.terracotta.command.EntityCountCommand
import com.destroystokyo.paper.event.block.TNTPrimeEvent
import com.destroystokyo.paper.event.block.TNTPrimeEvent.PrimeReason.EXPLOSION
import org.bukkit.Bukkit
import org.bukkit.event.Listener

class Terracotta: KotlinPlugin(), Listener {

    companion object {
        fun getViewDistance(): Double {
            return Bukkit.getServer().viewDistance * 16.0
        }
    }

    override fun onPluginEnable() {
        this.getCommand("terrabutcher")?.setExecutor(ButcherCommand)
        this.getCommand("terracounter")?.setExecutor(EntityCountCommand)
        this.events {
            // prevent TNT chain reactions
            event<TNTPrimeEvent> {
                if (reason == EXPLOSION) isCancelled = true
            }
        }
    }

}
