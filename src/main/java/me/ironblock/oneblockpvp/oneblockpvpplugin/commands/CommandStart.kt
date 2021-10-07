package me.ironblock.oneblockpvp.oneblockpvpplugin.commands

import me.ironblock.oneblockpvp.oneblockpvpplugin.event.EventListener
import me.ironblock.oneblockpvp.oneblockpvpplugin.mapGeneration.NetherMapGenerator
import me.ironblock.oneblockpvp.oneblockpvpplugin.mapGeneration.OverWorldMapGenerator
import me.ironblock.oneblockpvp.oneblockpvpplugin.mapGeneration.TheEndMapGenerator
import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.Material
import org.bukkit.advancement.AdvancementProgress
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Item
import org.bukkit.inventory.ItemStack
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType


object CommandStart : CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        val width: Int
        val height: Int
        if (args.size >= 2) {
            try {
                width = args[0].toInt()
                height = args[1].toInt()
            } catch (e: Exception) {
                sender.sendMessage("请输入数字")
                return true
            }
        } else {
            width = 200
            height = 200
        }
        val start = System.currentTimeMillis()
        OverWorldMapGenerator.generateMap(Bukkit.getWorld("world")!!, width, height)
        NetherMapGenerator.generateMap(Bukkit.getWorld("world_nether")!!, width, height)
        TheEndMapGenerator.generateMap(Bukkit.getWorld("world_the_end")!!, width, height)
        val bl: Boolean = if (args.size > 2) {
            args[2].toBoolean()
        } else {
            false
        }

        for (entry in Bukkit.getScoreboardManager()!!.mainScoreboard.getTeam("died")!!.entries) {
            Bukkit.getScoreboardManager()!!.mainScoreboard.getTeam("died")!!.removeEntry(entry)
        }
        OverWorldMapGenerator.spreadPlayers(Bukkit.getOnlinePlayers(), bl)
        val end = System.currentTimeMillis()
        EventListener.resetPlayerDeadStats()
        for (world in Bukkit.getWorlds()) {
            for (entitiesByClass in world.getEntitiesByClass(Item::class.java)) {
                entitiesByClass.remove()
            }
        }
        for (onlinePlayer in Bukkit.getOnlinePlayers()) {
            for (activePotionEffect in onlinePlayer.activePotionEffects) {
                onlinePlayer.removePotionEffect(activePotionEffect.type)
            }
            onlinePlayer.gameMode = GameMode.SURVIVAL
            onlinePlayer.inventory.clear()
            onlinePlayer.inventory.addItem(ItemStack(Material.WOODEN_PICKAXE))
            onlinePlayer.addPotionEffect(PotionEffect(PotionEffectType.SATURATION, 60, 1, true, false, false))
            onlinePlayer.addPotionEffect(PotionEffect(PotionEffectType.FIRE_RESISTANCE, 60, 1, true, false, false))
            onlinePlayer.addPotionEffect(PotionEffect(PotionEffectType.NIGHT_VISION, 9999999, 1, true, false, false))
            onlinePlayer.isInvulnerable = false
        }
        val iterator = Bukkit.getServer().advancementIterator()
        while (iterator.hasNext()) {
            val itr = iterator.next()
            for (onlinePlayer in Bukkit.getOnlinePlayers()) {
                val progress: AdvancementProgress = onlinePlayer.getAdvancementProgress(itr)
                for (criteria in progress.awardedCriteria) progress.revokeCriteria(criteria!!)
            }

        }
        println("地形生成完成,耗时${end - start}毫秒")
        EventListener.isGameStarted = true
        return true
    }
}