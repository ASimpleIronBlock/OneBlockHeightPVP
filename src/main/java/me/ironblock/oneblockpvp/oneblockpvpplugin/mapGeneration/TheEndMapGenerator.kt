package me.ironblock.oneblockpvp.oneblockpvpplugin.mapGeneration

import org.bukkit.Bukkit
import org.bukkit.World

object TheEndMapGenerator :MapGenerator(){



    override fun getWorld(): World {
        return Bukkit.getWorld("world_the_end")!!
    }
}