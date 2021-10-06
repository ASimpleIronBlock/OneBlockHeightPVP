package me.ironblock.oneblockpvp.oneblockpvpplugin.commands

import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender

object CommandAutoStart:CommandExecutor {
    var autoStart = true
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if(args.size==1){
            try {
                val bl = args[0].toBoolean()
                autoStart = bl
                if (autoStart){
                    sender.sendMessage("已开启自动开始游戏")
                }else{
                    sender.sendMessage("已停止自动开始游戏")
                }
            } catch (e: Exception) {
                sender.sendMessage("请输入true/false")
            }
        }
        return true

    }


}