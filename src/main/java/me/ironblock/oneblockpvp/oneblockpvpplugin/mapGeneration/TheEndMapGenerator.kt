package me.ironblock.oneblockpvp.oneblockpvpplugin.mapGeneration

import org.bukkit.Bukkit
import org.bukkit.World

object TheEndMapGenerator :MapGenerator(){

    override fun generateMap(worldIn: World, width: Int, height: Int) {
        super.generateMap(worldIn, width, height, generatedBlocks)
    }


    override fun getWorld(): World {
        return Bukkit.getWorld("world_the_end")!!
    }
}