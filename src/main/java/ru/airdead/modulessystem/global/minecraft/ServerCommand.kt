package ru.airdead.modulessystem.global.minecraft

import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.TabExecutor
import org.bukkit.entity.Player
import java.util.HashMap
import java.util.UUID

abstract class ServerCommand : TabExecutor {

    abstract val name: String
    abstract fun getUsage(): String
    abstract val permissionNode: String?
    abstract val isConsoleFriendly: Boolean
    abstract val argsRequirement: Int?
    open val cooldown: Long = 0L

    private val cooldowns = HashMap<UUID, Long>()

    companion object {
        private const val MILLIS_PER_SECOND = 1000
    }

    override fun onCommand(commandSender: CommandSender, command: Command, s: String, strings: Array<out String>): Boolean {
        if (!isConsoleFriendly && commandSender !is Player) {
            commandSender.sendMessage("Эта команда недоступна из консоли.")
            return true
        }

        if (commandSender is Player) {
            val playerId = commandSender.uniqueId
            cooldowns[playerId]?.let {
                val timeElapsed = System.currentTimeMillis() - it
                if (timeElapsed < cooldown * MILLIS_PER_SECOND) {
                    commandSender.sendMessage("§cПожалуйста, подождите ${((cooldown * MILLIS_PER_SECOND - timeElapsed) / MILLIS_PER_SECOND)} секунд, прежде чем использовать эту команду снова.")
                    return true
                }
            }
            cooldowns[playerId] = System.currentTimeMillis()
        }

        if (permissionNode != null) {
            val permission = permissionNode
            if (!commandSender.hasPermission(permission!!)) {
                commandSender.sendMessage("У вас нет разрешения использовать эту команду.")
                return true
            }
        }


        if(strings.isNotEmpty() && argsRequirement != null && strings.size != argsRequirement) {
            commandSender.sendMessage(getUsage())
            return true
        }

        return try {
            onCommand(CommandExecution(commandSender, strings))
            true
        } catch (e: Exception) {
            commandSender.sendMessage(getUsage())
            false
        }
    }

    abstract fun onCommand(execution: CommandExecution): Boolean

    override fun onTabComplete(sender: CommandSender, command: Command, alias: String, args: Array<out String>): List<String> {
        return onTabComplete(CommandExecution(sender, args))
    }

    abstract fun onTabComplete(execution: CommandExecution): List<String>

    class ThrowUsage : RuntimeException()
}
