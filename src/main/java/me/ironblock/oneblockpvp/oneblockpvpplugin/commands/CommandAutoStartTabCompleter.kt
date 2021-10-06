package me.ironblock.oneblockpvp.oneblockpvpplugin.commands

import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter

object CommandAutoStartTabCompleter:TabCompleter {
    override fun onTabComplete(
        sender: CommandSender,
        command: Command,
        alias: String,
        args: Array<out String>
    ): MutableList<String> {
        if (args.size==1){
            return mutableListOf("true","false")
        }
        return mutableListOf()
    }
}