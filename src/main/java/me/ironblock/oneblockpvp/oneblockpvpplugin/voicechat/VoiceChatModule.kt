package me.ironblock.oneblockpvp.oneblockpvpplugin.voicechat

import me.ironblock.oneblockpvp.oneblockpvpplugin.OneBlockPvPPlugin
import org.bukkit.Bukkit
import org.bukkit.block.data.type.Switch
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import java.io.File
import javax.sound.sampled.AudioFormat

object VoiceChatModule:Listener{
    private const val voiceData = "voicechat:voicedata"
    private const val settings = "voicechat:setting"

    val noChannelPlayerList = mutableListOf<Player>()
    val teamChannelPlayerList = mutableListOf<Player>()
    val allChannelPlayerList = mutableListOf<Player>()

    val audioFormat = AudioFormat(8000.0f, 8, 1, true, false)
    fun init(){
        OneBlockPvPPlugin.instance!!.server.messenger
            .registerIncomingPluginChannel(OneBlockPvPPlugin.instance!!, voiceData
            ) { channel: String?, player: Player?, message: ByteArray? ->
                this.handleVoiceData(
                    channel,
                    player,
                    message
                )
            }
        OneBlockPvPPlugin.instance!!.server.messenger
            .registerOutgoingPluginChannel(OneBlockPvPPlugin.instance!!, voiceData)
        OneBlockPvPPlugin.instance!!.server.messenger
            .registerIncomingPluginChannel(OneBlockPvPPlugin.instance!!, settings
            ) { channel: String?, player: Player?, message: ByteArray? ->
                this.handleClientSettings(
                    channel,
                    player,
                    message
                )
            }
        OneBlockPvPPlugin.instance!!.server.messenger
            .registerOutgoingPluginChannel(OneBlockPvPPlugin.instance!!, settings)

//        Thread(this::playerSong).start()
    }

    @EventHandler
    fun onPlayerJoin(event:PlayerJoinEvent){
        event.player.sendPluginMessage(OneBlockPvPPlugin.instance!!, settings, byteArrayOf(10))
        noChannelPlayerList.add(event.player)
    }


    /*
     * 如果客户端设置:
     *      C->S
     *      1:下一个频道
     *      2:上一个频道
     *      S->C
     *      1:禁言
     *      2:解除禁言
     */
    /**
     * 设置客户端禁言状态 stat:true为禁言,false为解除禁言
     */
    fun setMuteData(player:Player,stat:Boolean){
        player.sendPluginMessage(OneBlockPvPPlugin.instance!!, settings, byteArrayOf(if (stat)1 else 2))
    }

    private fun handleVoiceData(channel: String?, player: Player?, message: ByteArray?){
        if (allChannelPlayerList.contains(player!!)){

        }
//        player.sendPluginMessage(OneBlockPvPPlugin.instance!!, voiceData, message)
    }
    private fun handleClientSettings(channel: String?, player: Player?, message: ByteArray?){
        if (message!!.size==1){
            when(message[0]){
                0.toByte() ->{
                    noChannelPlayerList.add(player!!)
                    teamChannelPlayerList.remove(player)
                    allChannelPlayerList.remove(player)
                    player.sendPluginMessage(OneBlockPvPPlugin.instance!!, settings, byteArrayOf(10))
                }
                1.toByte()->{
                    noChannelPlayerList.remove(player)
                    teamChannelPlayerList.add(player!!)
                    allChannelPlayerList.remove(player)
                    player.sendPluginMessage(OneBlockPvPPlugin.instance!!, settings, byteArrayOf(11))
                }
                2.toByte()->{
                    noChannelPlayerList.remove(player)
                    teamChannelPlayerList.remove(player)
                    allChannelPlayerList.add(player!!)
                    player.sendPluginMessage(OneBlockPvPPlugin.instance!!, settings, byteArrayOf(12))
                }

            }
        }
    }
    private fun playerSong(){
        while (true){
            try {
                val input = ExternalAudioLoader.loadAudio(File("G:\\CloudMusic\\アニメ(ACG) - 白鹭归庭.wav"), audioFormat)
                var read = 0
                val buffer = ByteArray(800)
                while (run {
                        read = input.read(buffer)
                        read
                    } !=-1){
                    for (onlinePlayer in allChannelPlayerList) {
                        onlinePlayer.sendPluginMessage(OneBlockPvPPlugin.instance!!, voiceData , buffer)
                    }
                    Thread.sleep(100)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }
    }


}