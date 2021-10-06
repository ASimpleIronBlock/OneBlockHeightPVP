package me.ironblock.oneblockpvp.oneblockpvpplugin.utils

import me.ironblock.oneblockpvp.oneblockpvpplugin.mapGeneration.MapGenerator
import me.ironblock.oneblockpvp.oneblockpvpplugin.mapGeneration.NetherMapGenerator
import me.ironblock.oneblockpvp.oneblockpvpplugin.mapGeneration.OverWorldMapGenerator
import me.ironblock.oneblockpvp.oneblockpvpplugin.mapGeneration.TheEndMapGenerator
import org.bukkit.World
import org.bukkit.entity.Player

object Utils {

    fun World.getNextWorldMapGenerator(): MapGenerator {
        return when (this.name) {
            "world" -> NetherMapGenerator
            "world_nether" ->
                TheEndMapGenerator
            else ->
                OverWorldMapGenerator
        }
    }


}