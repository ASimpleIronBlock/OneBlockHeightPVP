package me.ironblock.oneblockpvp.oneblockpvpplugin.utils

import me.ironblock.oneblockpvp.oneblockpvpplugin.mapGeneration.OverWorldMapGenerator
import me.ironblock.oneblockpvp.oneblockpvpplugin.mapGeneration.NetherMapGenerator
import me.ironblock.oneblockpvp.oneblockpvpplugin.mapGeneration.TheEndMapGenerator
import me.ironblock.oneblockpvp.oneblockpvpplugin.mapGeneration.MapGenerator
import org.bukkit.Location
import org.bukkit.World
import java.util.*
import kotlin.math.cos
import kotlin.math.sin

object RandomLocation {
    fun randomLocation(width: Double, height: Double,dimension:World): Location{
        val radian = Random().nextDouble() * 114514.0
        val x = width / 2 * 0.8 * cos(radian) + width / 2
        val z = height / 2 * 0.8 * sin(radian) + height / 2
        val location = Location(dimension, 0.0, 0.0, 0.0)
        location.x = x
        location.y = (MapGenerator.y + 1).toDouble()
        location.z = z
        return location
    }


    fun randomLocation(width: Double, height: Double, crossDimension: Boolean,defaultDimension:World): Location {
        val radian = Random().nextDouble() * 114514.0
        val x = width / 2 * 0.8 * cos(radian) + width / 2
        val z = height / 2 * 0.8 * sin(radian) + height / 2
        val location = Location(OverWorldMapGenerator.getWorld(), 0.0, 0.0, 0.0)
        if (crossDimension) {
            when (Random().nextInt(3)) {
                0 -> location.world = OverWorldMapGenerator.getWorld()
                1 -> location.world = NetherMapGenerator.getWorld()
                2 -> location.world = TheEndMapGenerator.getWorld()
            }
        } else {
            location.world = defaultDimension
        }
        location.x = x
        location.y = (MapGenerator.y + 1).toDouble()
        location.z = z
        return location
    }
}