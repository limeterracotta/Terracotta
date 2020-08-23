package com.bermudalocket.terracotta

import com.bermudalocket.terracotta.command.ButcherCommand
import com.bermudalocket.terracotta.command.EntityCountCommand
import kotlinx.collections.immutable.persistentMapOf
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.Nameable
import org.bukkit.enchantments.Enchantment.*
import org.bukkit.entity.*
import org.bukkit.event.entity.CreatureSpawnEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.java.JavaPlugin

fun minutes(fromMinutes: Int): Int {
    return 20 * 60 * fromMinutes
}

class Terracotta: JavaPlugin() {

    override fun onEnable() {
        this.getCommand("terrabutcher")?.setExecutor(ButcherCommand)
        this.getCommand("terracounter")?.setExecutor(EntityCountCommand)
        this.server.scheduler.runTaskTimer(this, Runnable { commitAgeicide() }, minutes(1).toLong(), minutes(1).toLong())
    }

    companion object {
        fun getViewDistance(): Double {
            return Bukkit.getServer().viewDistance * 16.0
        }

        private fun LivingEntity.isOlderThanFiveMinutes(): Boolean {
            return this.ticksLived > 6_000
        }

        private fun ItemStack.isEnchanted(): Boolean {
            return this.enchantments.isNotEmpty()
        }

        private val GOOD_ENCHANTS = persistentMapOf(
            MENDING to 1,
            DAMAGE_ALL to 5,
            ARROW_INFINITE to 1,
            DAMAGE_UNDEAD to 5,
            ARROW_DAMAGE to 5,
            DURABILITY to 3,
            LOOT_BONUS_MOBS to 3,
            LOOT_BONUS_BLOCKS to 3,
            PROTECTION_ENVIRONMENTAL to 4,
            PROTECTION_EXPLOSIONS to 4,
            PROTECTION_FALL to 4,
            PROTECTION_FIRE to 4,
            PROTECTION_PROJECTILE to 4,
        )

        private fun LivingEntity.hasGoodEquipment(): Boolean {
            val equips = this.equipment ?: return false
            if (equips.armorContents.any { it.type == Material.ELYTRA }) return true
            val equipsWithGoodEnchants = equips.armorContents
                    .plus(equips.itemInMainHand)
                    .plus(equips.itemInOffHand)
                    .filter { it.isEnchanted() }
                    .filter { equip -> GOOD_ENCHANTS.any { equip.containsEnchantment(it.key) && equip.getEnchantmentLevel(it.key) >= it.value } }
                    .size
            return equipsWithGoodEnchants > 0
        }

        fun commitAgeicide() {
            println("Starting age check.")
            val start = System.currentTimeMillis()

            val entities = Bukkit.getWorlds().flatMap { it.livingEntities }.asSequence()
            println("There are ${entities.count()} entities to consider.")

            val typeMatch = entities.filter {
                it is Hoglin || it is Piglin || it is Fish || it is Squid ||
                it is PigZombie || it is Zombie || it is Skeleton || it is Spider ||
                it is Creeper || it is Witch || it is Zoglin
            }
            println("There are ${typeMatch.count()} entities with eligible types.")

            val notSpawnedByEggOrBucket = typeMatch.filter {
                it.entitySpawnReason != CreatureSpawnEvent.SpawnReason.SPAWNER_EGG
            }
            println("... ${typeMatch.count() - notSpawnedByEggOrBucket.count()} are ineligible because they were spawned by an egg or bucket.")

            val oldEnough = notSpawnedByEggOrBucket.filter {
                it.isOlderThanFiveMinutes()
            }
            println("... ${notSpawnedByEggOrBucket.count() - oldEnough.count()} are ineligible because they are too young.")

            val notNamed = oldEnough.filterNot {
                it is Nameable && it.customName != null
            }
            println("... ${oldEnough.count() - notNamed.count()} are ineligible because they are named.")

            notNamed.filterNot {
                        it.hasGoodEquipment()
                    }
                    .toList()
                    .forEach {
                        println("Age-culled ${it.type} at (${it.location.blockX}, ${it.location.blockY}, ${it.location.blockZ}).")
                        it.remove()
                    }
            val delta = System.currentTimeMillis() - start
            println("Finished age check (took $delta ms).")
        }
    }

}
