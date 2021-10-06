package me.ironblock.oneblockpvp.oneblockpvpplugin.utils

import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.EnchantmentStorageMeta
import java.util.*
import java.util.concurrent.atomic.AtomicReference

object DropUtils {
    private var map = mutableMapOf<ItemStack,Double>()
    fun getRanDrop():ItemStack{
        val itemStack = random(map)!!
        if (itemStack.type == Material.ENCHANTED_BOOK){
            val ran = (Math.random()*2).toInt()+1
                val meta = itemStack.itemMeta as EnchantmentStorageMeta
                    for (enchant in meta.storedEnchants) {
                        meta.removeStoredEnchant(enchant.key)
                    }
                    meta.addStoredEnchant(randomEnchant(),ran,true)
                itemStack.itemMeta = meta
        }
        return itemStack
    }
    private fun random(mapIn: Map<ItemStack, Double>): ItemStack? {
        var ran = Math.random()
        val m = AtomicReference<ItemStack?>()
        mapIn.forEach { (key: ItemStack?, value: Double) ->
            if (ran < value) {
                m.set(key)
                return key
            }
            ran -= value
        }
        return m.get()
    }
    private fun normalizeBlockPossibilityMap(mapIn: Map<ItemStack, Double>): MutableMap<ItemStack, Double> {
        val sum = mapIn.values.stream().mapToDouble { a: Double? -> a!! }.sum()
        val map = mutableMapOf<ItemStack, Double>()
        mapIn.forEach { (key, value) ->
            run {
                map[key] = value / sum
            }
        }
        return map
    }

    private fun randomEnchant():Enchantment{
        return Enchantment.values()[(Math.random()*Enchantment.values().size).toInt()]
    }


    fun loadFromConfig(configuration: Properties){
        map.clear()
        for (entry in configuration.entries) {
            val split = entry.key.toString().split(",")
            val material = Material.valueOf(split[0])
            val count = split[1].toInt()
            val possibility = entry.value.toString().toDouble()

            val itemStack = ItemStack(material,count)
            map[itemStack] = possibility
        }

        map = normalizeBlockPossibilityMap(map).toMutableMap()
    }
}