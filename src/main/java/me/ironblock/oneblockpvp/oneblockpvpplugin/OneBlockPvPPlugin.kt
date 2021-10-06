package me.ironblock.oneblockpvp.oneblockpvpplugin

import me.ironblock.oneblockpvp.oneblockpvpplugin.commands.CommandAutoStart
import me.ironblock.oneblockpvp.oneblockpvpplugin.commands.CommandAutoStartTabCompleter
import me.ironblock.oneblockpvp.oneblockpvpplugin.commands.CommandStart
import me.ironblock.oneblockpvp.oneblockpvpplugin.commands.CommandStartTabCompleter
import me.ironblock.oneblockpvp.oneblockpvpplugin.event.EventListener
import me.ironblock.oneblockpvp.oneblockpvpplugin.mapGeneration.NetherMapGenerator
import me.ironblock.oneblockpvp.oneblockpvpplugin.mapGeneration.OverWorldMapGenerator
import me.ironblock.oneblockpvp.oneblockpvpplugin.mapGeneration.TheEndMapGenerator
import me.ironblock.oneblockpvp.oneblockpvpplugin.utils.DropUtils
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.entity.Minecart
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.scheduler.BukkitRunnable
import java.io.File
import java.io.FileInputStream
import java.nio.file.Files
import java.util.*

class OneBlockPvPPlugin : JavaPlugin() {

    companion object {
        var instance: OneBlockPvPPlugin? = null
        var config: FileConfiguration? = null

    }

    init {
        instance = this
    }

    override fun onEnable() {
        OneBlockPvPPlugin.config = config
        loadConfig(config)
        server.pluginManager.registerEvents(EventListener, this)
        server.getPluginCommand("gameStart")!!.let {
            it.setExecutor(CommandStart)
            it.tabCompleter = CommandStartTabCompleter

        }
        server.getPluginCommand("autoStart")!!.let {
            it.setExecutor(CommandAutoStart)
            it.tabCompleter = CommandAutoStartTabCompleter

        }
        EventListener.init()
        object : BukkitRunnable() {
            override fun run() {
                EventListener.onTick()
            }
        }.runTaskTimer(this, 1, 1)
    }




    private fun loadConfig(config: FileConfiguration) {
        config.addDefault("OverWorldGenerationConfigFile", "OneBlockHeight/OverWorldGeneration.properties")
        config.addDefault("NetherGenerationConfigFile", "OneBlockHeight/NetherGeneration.properties")
        config.addDefault("TheEndGenerationConfigFile", "OneBlockHeight/TheEndGeneration.properties")
        config.addDefault("BarrelDropsConfigFile","OneBlockHeight/BarrelDrops.properties")
        config.addDefault("AutoStartGamePlayers", 5)
        config.addDefault("DeathModeTimer", 20 * 30)
        config.addDefault("DeathModeMapWidth", 20)
        config.addDefault("DeathModeMapHeight", 20)
        config.addDefault("RejoinTimer", 200)
        config.addDefault("GameStartTime", 600)
        config.addDefault("ScoreboardChangeTime", 100)
        config.addDefault("TheEndGlobalDamageMultiplier", 2.0)
        config.addDefault("TheEndEnvironmentalDamageMultiplier", 1.5)
        config.addDefault("TheEndPlayerDamageMultiplier", 1.5)
        config.addDefault("PreventLivingPlayerReceivingDeadPlayerMessage", true)

        config.options().copyDefaults(true)
        saveConfig()

        val overWorldConfigFile = checkAndCopy("OverWorldGenerationConfigFile","OverWorldGeneration.properties")
        val netherConfigFile = checkAndCopy("NetherGenerationConfigFile","NetherGeneration.properties")
        val theEndConfigFile = checkAndCopy("TheEndGenerationConfigFile","TheEndGeneration.properties")
        val barrelDrops = checkAndCopy("BarrelDropsConfigFile","BarrelDrops.properties")

        OverWorldMapGenerator.loadFromConfig(overWorldConfigFile)
        NetherMapGenerator.loadFromConfig(netherConfigFile)
        TheEndMapGenerator.loadFromConfig(theEndConfigFile)

        DropUtils.loadFromConfig(barrelDrops)




        EventListener.rejoinTime = config["RejoinTimer"].toString().toInt()
        EventListener.gameStartTime = config["GameStartTime"].toString().toInt()
        EventListener.deathModeStartTime = config["DeathModeTimer"].toString().toInt()
        EventListener.scoreBoardChangeTime = config["ScoreboardChangeTime"].toString().toInt()
        EventListener.theEndGlobalDamageMultiplier = config["TheEndGlobalDamageMultiplier"].toString().toDouble()
        EventListener.theEndPlayerDamageMultiplier = config["TheEndPlayerDamageMultiplier"].toString().toDouble()
        EventListener.theEndEnvironmentalDamageMultiplier = config["TheEndEnvironmentalDamageMultiplier"].toString().toDouble()
        EventListener.preventLivingPlayerReceivingDeadPlayerMessage = config["PreventLivingPlayerReceivingDeadPlayerMessage"].toString().toBoolean()
        EventListener.gameAutoStartPlayers = config["AutoStartGamePlayers"].toString().toInt()
        EventListener.deathModeWidth = config["DeathModeMapWidth"].toString().toInt()
        EventListener.deathModeHeight = config["DeathModeMapHeight"].toString().toInt()

    }

    private fun checkAndCopy(configRoot:String,copyPath:String):Properties{
        val properties = Properties()
        val configPath = File(dataFolder.absoluteFile,config[configRoot].toString())
        if (!configPath.exists()){
            configPath.parentFile.mkdirs()
            Files.copy(classLoader.getResourceAsStream(copyPath)!!,configPath.toPath())
        }
        val stream = FileInputStream(configPath)
        properties.load(stream)
        stream.close()
        return properties
    }


}