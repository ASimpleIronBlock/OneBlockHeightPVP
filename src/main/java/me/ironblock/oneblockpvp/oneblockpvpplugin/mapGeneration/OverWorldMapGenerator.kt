package me.ironblock.oneblockpvp.oneblockpvpplugin.mapGeneration

import org.bukkit.Bukkit
import org.bukkit.World

object OverWorldMapGenerator : MapGenerator() {


    override fun getWorld(): World {
        return Bukkit.getWorld("world")!!
    }
}