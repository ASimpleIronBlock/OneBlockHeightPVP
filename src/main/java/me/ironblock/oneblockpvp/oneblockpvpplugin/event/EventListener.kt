package me.ironblock.oneblockpvp.oneblockpvpplugin.event

import me.ironblock.oneblockpvp.oneblockpvpplugin.OneBlockPvPPlugin
import me.ironblock.oneblockpvp.oneblockpvpplugin.commands.CommandAutoStart
import me.ironblock.oneblockpvp.oneblockpvpplugin.commands.CommandStart
import me.ironblock.oneblockpvp.oneblockpvpplugin.mapGeneration.OverWorldMapGenerator
import me.ironblock.oneblockpvp.oneblockpvpplugin.mapGeneration.TheEndMapGenerator
import me.ironblock.oneblockpvp.oneblockpvpplugin.utils.DropUtils
import me.ironblock.oneblockpvp.oneblockpvpplugin.utils.Timer
import me.ironblock.oneblockpvp.oneblockpvpplugin.utils.Utils.getNextWorldMapGenerator
import net.md_5.bungee.api.ChatMessageType
import org.bukkit.*
import org.bukkit.entity.EnderCrystal
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.enchantment.PrepareItemEnchantEvent
import org.bukkit.event.entity.EntityDamageByBlockEvent
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.player.*
import org.bukkit.inventory.InventoryView
import org.bukkit.inventory.ItemStack
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import org.bukkit.scheduler.BukkitRunnable
import org.bukkit.scoreboard.DisplaySlot
import java.math.BigDecimal
import java.util.*


object EventListener : Listener {
    /*
     * 会在配置文件中被更改的属性
     */

    //掉线重连时间
    var rejoinTime: Int = 0

    //游戏开始时间
    var gameStartTime: Int = 0

    //死斗模式开始时间
    var deathModeStartTime: Int = 0

    //计分板内容改变时间
    var scoreBoardChangeTime: Int = 0


    //末地全局伤害乘数
    var theEndGlobalDamageMultiplier = 0.0

    //末地环境伤害乘数
    var theEndEnvironmentalDamageMultiplier = 0.0

    //末地玩家伤害乘数
    var theEndPlayerDamageMultiplier = 0.0

    //死后的玩家说话是否无法让没死的玩家收到
    var preventLivingPlayerReceivingDeadPlayerMessage = true

    //游戏自动开始的人数
    var gameAutoStartPlayers = 0

    //死斗模式的场地长
    var deathModeWidth = 0

    //死斗模式的场地宽
    var deathModeHeight = 0

    var spawnPoint = Location(OverWorldMapGenerator.getWorld(),100.0 ,241.0,100.0)
    /*
     * 游戏的一些状态
     */

    //游戏是否开始
    var isGameStarted = false

    //是否在死斗模式
    private var deathMode = false


    private val crossDimensionTeleportPlayersMap = mutableMapOf<Player, Timer>()

    //游戏开始的计时器
    private var gameStartTimer: Timer? = null

    //死了的玩家
    private val deadPlayers = mutableSetOf<Player>()

    //掉线并等待重连的玩家
    private val quitPlayers = mutableMapOf<UUID, Timer>()

    //死斗模式的强制传送计时器
    private var deathModeTimer: Timer? = null

    //计分板轮换计时器
    private val scoreBoardTimer = Timer(scoreBoardChangeTime)

    //计分板轮换的内容
    private val scoreBoardDisplayList = listOf("kill", "death", "win", "fish")

    //计分板轮换到了第几个
    private var scoreBoarderDisplaying = 0

    //计分板轮换计时器

    @EventHandler
    fun playerJoinEvent(event: PlayerJoinEvent) {
        if (!quitPlayers.containsKey(event.player.uniqueId)) {
            //不是掉线了的玩家
            if (isGameStarted) {

                //游戏开始时加入
                deadPlayers.add(event.player)
                event.player.gameMode = GameMode.SPECTATOR
            } else {

                //游戏未开始时加入
                event.player.isInvulnerable = true
                event.player.gameMode = GameMode.ADVENTURE
            }

            //传送
            val location = spawnPoint
            event.player.teleport(location)
            event.player.removePotionEffect(PotionEffectType.GLOWING)
            object : BukkitRunnable() {
                override fun run() {
                    (event.player.teleport(location))
                }
            }.runTaskLater(OneBlockPvPPlugin.instance!!, 10)


        } else {
            //断线重连
            quitPlayers.remove(event.player.uniqueId)
            Bukkit.broadcastMessage("${event.player.name}重新加入了游戏")
        }


    }

    @EventHandler
    fun onPlayerQuit(event: PlayerQuitEvent) {
        if (isGameStarted) {
            if (!deadPlayers.contains(event.player)) {
                quitPlayers[event.player.uniqueId] = Timer(rejoinTime)
                Bukkit.broadcastMessage("${event.player.name}掉线了,他有${rejoinTime/20}秒钟的时间重新加入游戏")
            }
        }
    }

    @EventHandler
    fun onPlayerFish(event: PlayerFishEvent) {
        if (event.state == PlayerFishEvent.State.CAUGHT_FISH)
            Bukkit.getScoreboardManager()!!.mainScoreboard.getObjective("fish")!!.getScore(event.player.name).score++
    }

    @EventHandler
    fun onBlockBroken(event: BlockBreakEvent) {
        when (event.block.type) {
            //禁止破坏附魔台
            Material.ENCHANTING_TABLE -> {
                event.player.sendMessage(ChatColor.RED.toString() + "请不要破坏附魔台!")
                event.isCancelled = true
            }
            //破坏木桶掉落东西
            Material.BARREL -> {
                event.isCancelled = true
                event.block.type = Material.AIR
                event.block.world.dropItem(event.block.location, DropUtils.getRanDrop())
            }
            //挖地狱疣块变成地狱疣
            Material.NETHER_WART_BLOCK -> {
                event.isCancelled = true
                event.block.type = Material.AIR
                event.block.world.dropItem(event.block.location, ItemStack(Material.NETHER_WART, 9))
            }
            //挖树叶变成苹果
            Material.OAK_LEAVES -> {
                event.isCancelled = true
                event.block.type = Material.AIR
                event.block.world.dropItem(event.block.location, ItemStack(Material.APPLE, 1))
            }
            //挖砂砾变成燧石
            Material.GRAVEL -> {
                event.isCancelled = true
                event.block.type = Material.AIR
                event.block.world.dropItem(event.block.location, ItemStack(Material.FLINT, 1))
            }

            //我不想看见warning
            else -> {
            }
        }

    }

    @EventHandler
    fun onBlockRightClicked(event: PlayerInteractEvent) {
        //判断点击到了东西
        event.clickedBlock ?: return

        //木桶掉落
        if (event.clickedBlock!!.type == Material.BARREL && event.action == Action.RIGHT_CLICK_BLOCK && event.player.gameMode != GameMode.SPECTATOR) {
            event.isCancelled = true
            event.clickedBlock!!.type = Material.AIR
            event.clickedBlock!!.world.dropItem(event.clickedBlock!!.location, DropUtils.getRanDrop())
        }
        event.item ?: return

        //只有末地能放末影水晶
        if (event.clickedBlock!!.type == Material.BARRIER && event.item!!.type == Material.END_CRYSTAL && event.action == Action.RIGHT_CLICK_BLOCK && event.player.world == TheEndMapGenerator.getWorld()) {
            val crystal = event.player.world.spawnEntity(
                event.clickedBlock!!.location.add(0.5, 1.0, 0.5),
                EntityType.ENDER_CRYSTAL
            ) as EnderCrystal
            crystal.isShowingBottom = false
            event.item!!.amount--
            event.useItemInHand()

        }
    }

    @EventHandler
    fun onEnchant(event: PrepareItemEnchantEvent) {
        //把所有附魔选项都设成30级
        val view = event.view
        view.setProperty(InventoryView.Property.ENCHANT_BUTTON1, 30)
        view.setProperty(InventoryView.Property.ENCHANT_BUTTON2, 30)
        view.setProperty(InventoryView.Property.ENCHANT_BUTTON3, 30)

    }

    @EventHandler
    fun onPlayerDeath(event: PlayerDeathEvent) {
        if (isGameStarted) {
            event.entity.gameMode = GameMode.SPECTATOR
            deadPlayers.add(event.entity)
        }
        event.entity.setBedSpawnLocation(spawnPoint, true)
    }

    @EventHandler
    fun onPlayerHurt(event: EntityDamageEvent) {
        //末地全局伤害乘数
        if (event.entity.world == TheEndMapGenerator.getWorld() && isGameStarted) {
            event.damage *= theEndGlobalDamageMultiplier
        }
    }

    @EventHandler
    fun onPlayerHurtByEntity(event: EntityDamageByEntityEvent) {

        if (event.entity.world == TheEndMapGenerator.getWorld() && isGameStarted) {
            //被实体伤害
            event.damage *= theEndPlayerDamageMultiplier
        }
    }

    @EventHandler
    fun onPlayerHurtByBlock(entityDamageEvent: EntityDamageByBlockEvent) {
        if (entityDamageEvent.entity.world == TheEndMapGenerator.getWorld() && isGameStarted) {
            //被环境伤害
            entityDamageEvent.damage *= theEndEnvironmentalDamageMultiplier
        }
    }

    @EventHandler
    fun onPlayerAttack(event: EntityDamageByEntityEvent) {

        //阻止死了的玩家攻击活着的玩家
        if (event.damager is Player && deadPlayers.contains(event.damager) && event.entity is Player && !deadPlayers.contains(
                event.entity
            )
        ) {
            event.isCancelled = true
            event.damager.sendMessage(ChatColor.RED.toString() + "请不要干扰正常游戏的玩家")
            println("死了的玩家${event.damager.name}尝试攻击活着的玩家${event.entity.name}")
        }
    }


    @EventHandler
    fun onPlayerSendChatMessage(event: AsyncPlayerChatEvent) {

        if (preventLivingPlayerReceivingDeadPlayerMessage) {
            if (deadPlayers.contains(event.player)) {       //死的人说话只可以让死的人听到
                event.recipients.clear()
                event.recipients.addAll(deadPlayers)
                println(event.recipients.toTypedArray().contentToString())
            } else {
                event.recipients.addAll(Bukkit.getOnlinePlayers())  //活的人说话全能听见
            }
        }
    }


    fun onTick() {
        if (scoreBoardTimer.update()) {
            //计分板轮换
            Bukkit.getScoreboardManager()?.mainScoreboard!!.getObjective(scoreBoardDisplayList[scoreBoarderDisplaying])!!.displaySlot =
                DisplaySlot.SIDEBAR
            scoreBoarderDisplaying += 1
            if (scoreBoarderDisplaying >= scoreBoardDisplayList.size) {
                scoreBoarderDisplaying = 0
            }
        }


        if (isGameStarted) {

            //把死了玩家添加到died队伍里
            for (deadPlayer in deadPlayers) {
                if (!Bukkit.getScoreboardManager()!!.mainScoreboard.getTeam("died")!!.hasEntry(deadPlayer.name)) {
                    Bukkit.getScoreboardManager()!!.mainScoreboard.getTeam("died")!!.addEntry(deadPlayer.name)
                }
            }


            //检测断线时间是否超时
            val itr = quitPlayers.iterator()
            var player: Map.Entry<UUID, Timer>?
            while (itr.hasNext()) {
                player = itr.next()
                if (player.value.update()) {
                    itr.remove()
                }
            }

            //获取还活着的人数
            val online = Bukkit.getOnlinePlayers().toMutableSet()
            online.removeAll(deadPlayers)

            //掉线人数为0时
            if (quitPlayers.isEmpty()) {
                //胜利
                if (online.size == 1) {
                    for (pl in online) {
                        victory(pl)
                    }
                } else if (online.size == 0) {
                    //全死光了 平局
                    draw()
                } else if (online.size <= 3) {
                    //人数在3个以下 全体发光
                    for (pl in online) {
                        pl.spigot().sendMessage(
                            ChatMessageType.ACTION_BAR,
                            net.md_5.bungee.api.chat.TextComponent(ChatColor.GREEN.toString() + "目前所有玩家已发光")
                        )
                        pl.addPotionEffect(PotionEffect(PotionEffectType.GLOWING, 3061, 1, true, false, false))
                    }
                }

            }
            if (online.size == 2 && !deathMode) {
                if (deathModeTimer == null) {
                    Bukkit.broadcastMessage("场上只剩两个人了,${deathModeStartTime / 20}秒后将强制tp")
                    deathModeTimer = Timer(deathModeStartTime)
                } else {
                    if (deathModeTimer!!.update()) {
                        deathMode()
                        deathMode = true
                        deathModeTimer!!.reset()
                    }
                    for (onlinePlayer in Bukkit.getOnlinePlayers()) {
                        onlinePlayer.spigot().sendMessage(
                            ChatMessageType.ACTION_BAR,
                            net.md_5.bungee.api.chat.TextComponent(ChatColor.GREEN.toString() + "距离强制tp还有${(deathModeTimer!!.timer - deathModeTimer!!.counter) / 20}秒!")
                        )
                    }

                }

            }

            //末地的玩家全部发光
            val playerInTheEnd = TheEndMapGenerator.getWorld().getEntitiesByClass(Player::class.java)
            if (playerInTheEnd.isNotEmpty()) {
                for (pl in playerInTheEnd) {
                    pl.addPotionEffect(PotionEffect(PotionEffectType.GLOWING, 3061, 1, true, false, false))
                }
            }


            for (onlinePlayer in Bukkit.getOnlinePlayers()) {
                //给了23:33的速度效果
                onlinePlayer.addPotionEffect(PotionEffect(PotionEffectType.SPEED, 3061, 0, true, false, false))
                onlinePlayer.level = 2333

                //对于活人的处理
                if (!deadPlayers.contains(onlinePlayer)) {
                    val players = onlinePlayer.world.getEntitiesByClasses(Player::class.java)
                    var minDist = 16.1
                    players.forEach{ pl ->
                        if (pl.uniqueId != onlinePlayer.uniqueId && !deadPlayers.contains(pl)) {
                            val dist = pl.location.distance(onlinePlayer.location)
                            if (dist < minDist) {
                                minDist = dist
                            }
                        }
                    }
                    if (minDist < 16) {

                        //16格之内有敌人 并在actionbar上显示
                        val b = BigDecimal(minDist)
                        val f1: Double = b.setScale(2, BigDecimal.ROUND_HALF_UP).toDouble()
                        onlinePlayer.spigot().sendMessage(
                            ChatMessageType.ACTION_BAR,
                            net.md_5.bungee.api.chat.TextComponent(ChatColor.GREEN.toString() + "距离你${f1}格的地方有敌人")
                        )
                    }
                }

                //附魔台传送
                if (onlinePlayer.location.block.type == Material.ENCHANTING_TABLE && !onlinePlayer.isGameFailed()) {
                    if (crossDimensionTeleportPlayersMap.containsKey(onlinePlayer)) {
                        if (crossDimensionTeleportPlayersMap[onlinePlayer]!!.update()) {
                            if (!deathMode) {
                                onlinePlayer.world.getNextWorldMapGenerator().teleportToEnchantTable(onlinePlayer)
                            } else {
                                onlinePlayer.sendMessage(ChatColor.RED.toString()+"死斗模式不可以使用附魔台传送!!")
                                crossDimensionTeleportPlayersMap[onlinePlayer]!!.reset()
                            }
                        }
                        onlinePlayer.spigot().sendMessage( ChatMessageType.ACTION_BAR,
                            net.md_5.bungee.api.chat.TextComponent(ChatColor.GREEN.toString() + "距离跨界传送还有${(crossDimensionTeleportPlayersMap[onlinePlayer]!!.timer - crossDimensionTeleportPlayersMap[onlinePlayer]!!.counter) / 20}秒!"))
                    } else {
                        crossDimensionTeleportPlayersMap[onlinePlayer] = Timer(100)
                    }
                } else {
                    if (crossDimensionTeleportPlayersMap.containsKey(onlinePlayer)) {
                        crossDimensionTeleportPlayersMap[onlinePlayer]!!.reset()
                    } else {
                        crossDimensionTeleportPlayersMap[onlinePlayer] = Timer(100)
                    }
                }
            }

        } else {


            if (CommandAutoStart.autoStart) {
                if (Bukkit.getOnlinePlayers().size > gameAutoStartPlayers) {
                    if (gameStartTimer == null) {
                        gameStartTimer = Timer(gameStartTime)
                        Bukkit.broadcastMessage("游戏将于${gameStartTime / 20}秒后开始")
                    } else {
                        if (gameStartTimer!!.update()) {
                            //大于十人就玩200x200的地图
                            if (Bukkit.getOnlinePlayers().size > 10) {
                                CommandStart.onCommand(
                                    Bukkit.getConsoleSender(),
                                    OneBlockPvPPlugin.instance!!.getCommand("gameStart")!!,
                                    "",
                                    arrayOf("200", "200", "true")
                                )
                            } else {
                                //小于10人就玩100x100的地图
                                CommandStart.onCommand(
                                    Bukkit.getConsoleSender(),
                                    OneBlockPvPPlugin.instance!!.getCommand("gameStart")!!,
                                    "",
                                    arrayOf("100", "100", "true")
                                )
                            }
                            gameStartTimer = null
                        }

                        for (onlinePlayer in Bukkit.getOnlinePlayers()) {
                            onlinePlayer.spigot().sendMessage(
                                ChatMessageType.ACTION_BAR,
                                net.md_5.bungee.api.chat.TextComponent(ChatColor.GREEN.toString() + "游戏将在${(gameStartTimer!!.timer - gameStartTimer!!.counter) / 20}秒后开始!")
                            )
                        }
                    }

                } else {
                    if (gameStartTimer != null) {
                        Bukkit.broadcastMessage("人数不足,无法自动开始游戏")
                        gameStartTimer = null
                    }
                }
            } else {
                gameStartTimer = null
            }
        }


    }

    fun resetPlayerDeadStats() {

        for (deadPlayer in deadPlayers) {
            Bukkit.getScoreboardManager()!!.mainScoreboard.getTeam("died")!!.removeEntry(deadPlayer.name)
        }
        deadPlayers.clear()
    }

    private fun Player.isGameFailed(): Boolean {
        return deadPlayers.contains(this)
    }

    private fun victory(winner: Player) {
        Bukkit.getScoreboardManager()!!.mainScoreboard.getObjective("win")!!.getScore(winner.name).score++
        OneBlockPvPPlugin.instance!!.server.broadcastMessage(ChatColor.GREEN.toString() + "恭喜${winner.name}获得了胜利")
        restart()
    }

    private fun draw() {
        OneBlockPvPPlugin.instance!!.server.broadcastMessage(ChatColor.GREEN.toString() + "没有人获得胜利")
        restart()
    }

    private fun restart() {
        isGameStarted = false
        deathMode = false
        deathModeTimer = null
        gameStartTimer = null
        val location = spawnPoint
        for (onlinePlayer in Bukkit.getOnlinePlayers()) {
            onlinePlayer.teleport(location)
            onlinePlayer.gameMode = GameMode.SPECTATOR
        }
        resetPlayerDeadStats()
        for (onlinePlayer in Bukkit.getOnlinePlayers()) {
            onlinePlayer.teleport(location)
            onlinePlayer.gameMode = GameMode.ADVENTURE
            onlinePlayer.isInvulnerable = true
            onlinePlayer.inventory.clear()
            onlinePlayer.addPotionEffect(PotionEffect(PotionEffectType.SATURATION, 60, 1, true, false, false))
            onlinePlayer.addPotionEffect(
                PotionEffect(
                    PotionEffectType.FIRE_RESISTANCE,
                    60,
                    1,
                    true,
                    false,
                    false
                )
            )
            onlinePlayer.addPotionEffect(
                PotionEffect(
                    PotionEffectType.NIGHT_VISION,
                    9999999,
                    1,
                    true,
                    false,
                    false
                )
            )
            onlinePlayer.removePotionEffect(PotionEffectType.GLOWING)
        }
    }

    private fun deathMode() {
        OverWorldMapGenerator.generateMap(OverWorldMapGenerator.getWorld(), deathModeWidth, deathModeHeight)
        val players = Bukkit.getOnlinePlayers().toMutableList()
        players.removeAll(deadPlayers)
        OverWorldMapGenerator.spreadPlayers(players, false)

    }


    fun init() {
        scoreBoardTimer.setTime(scoreBoardChangeTime)

    }



    /**
     * 对计分板进行初始化
     */
    fun scoreboardInit(){
        Bukkit.getScoreboardManager()!!.mainScoreboard.getObjective("fish")?:Bukkit.getScoreboardManager()!!.mainScoreboard.registerNewObjective("fish","trigger","钓鱼榜")
        Bukkit.getScoreboardManager()!!.mainScoreboard.getTeam("died")?: run {
            Bukkit.getScoreboardManager()!!.mainScoreboard.registerNewTeam("died")
            Bukkit.getScoreboardManager()!!.mainScoreboard.getTeam("died")!!.prefix = "[旁观者]"
        }

        Bukkit.getScoreboardManager()!!.mainScoreboard.getObjective("kill")?:Bukkit.getScoreboardManager()!!.mainScoreboard.registerNewObjective("kill","minecraft.custom:minecraft.player_kills","击杀榜")
        Bukkit.getScoreboardManager()!!.mainScoreboard.getObjective("death")?:Bukkit.getScoreboardManager()!!.mainScoreboard.registerNewObjective("death","deathCount","死亡榜")
        Bukkit.getScoreboardManager()!!.mainScoreboard.getObjective("win")?:Bukkit.getScoreboardManager()!!.mainScoreboard.registerNewObjective("win","trigger","获胜榜")

    }


}


