package me.ironblock.oneblockpvp.oneblockpvpplugin.mapGeneration

import com.google.common.collect.Maps
import me.ironblock.oneblockpvp.oneblockpvpplugin.OneBlockPvPPlugin
import me.ironblock.oneblockpvp.oneblockpvpplugin.utils.RandomLocation
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.World
import org.bukkit.entity.Player
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import org.bukkit.scheduler.BukkitRunnable
import java.util.*
import java.util.concurrent.atomic.AtomicReference

abstract class MapGenerator {
    companion object{
        const val y = 128  
    }

    var width = 0
    var height = 0

    protected var generatedBlocks: MutableMap<Material, Double> = Maps.newHashMap()
    abstract fun generateMap(worldIn: World, width: Int, height: Int)
    protected fun generateMap(worldIn: World, width: Int, height: Int, generatedBlocks: Map<Material, Double>) {
        this.width = width
        this.height = height
        replaceWithAir(worldIn, width, height)
        generateBedrock(worldIn, width, height)
        generateBarrier(worldIn, width, height)
        replaceWithBlock(worldIn, width, height, generatedBlocks)
        genEnchantTable(worldIn, width, height)
    }


    private fun replaceWithBlock(worldIn: World,width: Int,height: Int,generatedBlocks: Map<Material, Double>){
        for (i in 1..width) {
            for (j in 1..height) {
                worldIn.getBlockAt(i, y+1, j).type = random(generatedBlocks)!!
            }
        }
    }
    private fun replaceWithAir(worldIn: World,width: Int,height: Int){
        for (i in 1..width) {
            for (j in 1..height) {
//                if (worldIn.getBlockAt(i,y+1,j).type==Material.BARREL){
//                    (worldIn.getBlockAt(i, y+1, j).blockData as TileState).type = Material.AIR
//                }
                worldIn.getBlockAt(i, y+1, j).type = Material.AIR

            }
        }
    }


    private fun generateBedrock(worldIn: World, width: Int, height: Int) {
        //先在周围围一圈基岩
        for (i in 0..width + 1) {
            worldIn.getBlockAt(i, y, 0).type = Material.BEDROCK
            worldIn.getBlockAt(i, y, height + 1).type = Material.BEDROCK
            worldIn.getBlockAt(i, y+1, 0).type = Material.BEDROCK
            worldIn.getBlockAt(i, y+1, height + 1).type = Material.BEDROCK
        }
        for (i in 0..height + 1) {
            worldIn.getBlockAt(0, y, i).type = Material.BEDROCK
            worldIn.getBlockAt(width + 1, y, i).type = Material.BEDROCK
            worldIn.getBlockAt(0, y+1, i).type = Material.BEDROCK
            worldIn.getBlockAt(width + 1, y+1, i).type = Material.BEDROCK

        }

    }

    private fun genEnchantTable(worldIn: World, width: Int, height: Int) {
        worldIn.getBlockAt(width / 2, y, height / 2).type = Material.ENCHANTING_TABLE
        worldIn.getBlockAt(width / 2, y+1, height / 2).type = Material.AIR
        worldIn.getBlockAt(width / 2 - 1, y+1, height / 2 - 1).type = Material.AIR
        worldIn.getBlockAt(width / 2 - 1, y+1, height / 2).type = Material.AIR
        worldIn.getBlockAt(width / 2 - 1, y+1, height / 2 + 1).type = Material.AIR
        worldIn.getBlockAt(width / 2 + 1, y+1, height / 2 - 1).type = Material.AIR
        worldIn.getBlockAt(width / 2 + 1, y+1, height / 2).type = Material.AIR
        worldIn.getBlockAt(width / 2 + 1, y+1, height / 2 + 1).type = Material.AIR
        worldIn.getBlockAt(width / 2, y+1, height / 2 - 1).type = Material.AIR
        worldIn.getBlockAt(width / 2, y+1, height / 2 + 1).type = Material.AIR
        Bukkit.broadcastMessage("附魔台的坐标是:${width / 2},$y, ${height / 2}")
    }

    private fun generateBarrier(worldIn: World, width: Int, height: Int) {
        for (i in 0..width + 1) {
            for (j in 0..height + 1) {
                worldIn.getBlockAt(i, y+2, j).type = Material.BARRIER
            }
        }
        for (i in 0..width + 1) {
            for (j in 0..height + 1) {
                worldIn.getBlockAt(i, y, j).type = Material.BARRIER
            }
        }
    }

    private fun normalizeBlockPossibilityMap(mapIn: Map<Material, Double>): Map<Material, Double> {
        val sum = mapIn.values.stream().mapToDouble { a: Double? -> a!! }.sum()
        val map = mutableMapOf<Material, Double>()
        mapIn.forEach { (key, value) ->
            run {
                map[key] = value / sum
            }
        }
        return map
    }

    private fun random(mapIn: Map<Material, Double>): Material? {
        var ran = Math.random()
        val m = AtomicReference<Material?>()
        mapIn.forEach { (key: Material?, value: Double) ->
            if (ran < value) {
                m.set(key)
                return key
            }
            ran -= value
        }
        return m.get()
    }

    private fun init(generatedBlocks: Map<Material, Double>): Map<Material, Double> {
        return normalizeBlockPossibilityMap(generatedBlocks)
    }



    fun spreadPlayers(playerList: Collection<Player>, crossDimension:Boolean) {
        for (entity in playerList) {
            val location = RandomLocation.randomLocation(width.toDouble(), height.toDouble(),crossDimension,getWorld())
            val x = location.x
            val z = location.z
            (entity.teleport(location))
            object :BukkitRunnable(){
                override fun run() {
                    (entity.teleport(location))
                }
            }.runTaskLater(OneBlockPvPPlugin.instance!!,10)

            for (i in -3..3) {
                for (j in -3..3) {
                    if (location.world!!.getBlockAt((x + i).toInt(), y+1, (z + j).toInt()).type != Material.BEDROCK) {
                        location.world!!.getBlockAt((x + i).toInt(), y+1, (z + j).toInt()).type = Material.AIR
                    }
                }
            }
            for (i in -6..6) {
                for (j in -6..6) {
                    if (location.world!!.getBlockAt((x + i).toInt(), y+1, (z + j).toInt()).isLiquid) {
                        location.world!!.getBlockAt((x + i).toInt(), y+1, (z + j).toInt()).type = Material.AIR
                    }
                }
            }
        }

    }


    fun teleportToEnchantTable(player:Player){
        val location = Location(getWorld(),width.toDouble()/2,y.toDouble()+1,height.toDouble()/2)
        player.addPotionEffect(PotionEffect(PotionEffectType.DAMAGE_RESISTANCE,10*20,4))
        player.teleport(location)
        object :BukkitRunnable(){
            override fun run() {
                (player.teleport(location))
            }
        }.runTaskLater(OneBlockPvPPlugin.instance!!,10)
    }

    fun loadFromConfig(config:Properties){
        generatedBlocks.clear()
        for (entry in config.entries) {
            val material = Material.valueOf(entry.key.toString())
            val possibility = entry.value.toString().toDouble()
            generatedBlocks[material] = possibility
        }

        generatedBlocks = init(generatedBlocks).toMutableMap()
    }


    abstract fun getWorld():World


}