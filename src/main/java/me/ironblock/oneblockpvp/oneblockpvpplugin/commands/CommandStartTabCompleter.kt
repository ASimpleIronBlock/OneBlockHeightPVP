package me.ironblock.oneblockpvp.oneblockpvpplugin.commands

import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter

object CommandStartTabCompleter:TabCompleter {
    override fun onTabComplete(
        sender: CommandSender,
        command: Command,
        alias: String,
        args: Array<out String>
    ): MutableList<String> {
        return if (args.size<=2){
            mutableListOf("<numbers>")
        }else if (args.size==3){
            mutableListOf("true","false")
        }else{
            mutableListOf()
        }
    }
}