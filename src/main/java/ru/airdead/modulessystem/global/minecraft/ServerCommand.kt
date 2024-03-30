package ru.airdead.modulessystem.global.minecraft

import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.entity.Player
import java.util.*

abstract class ServerCommand : CommandExecutor, TabCompleter {

    abstract val name: String
    abstract fun getUsage(): String
    abstract val permissionNode: String?
    abstract val isConsoleFriendly: Boolean
    abstract val argsRequirement: Int?
    open val cooldown: Long = 0L

    private val cooldowns = HashMap<UUID, Long>()

    companion object {
        private const val MILLIS_PER_SECOND = 1000L
    }

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (!isConsoleFriendly && sender !is Player) {
            sender.sendMessage("Эта команда недоступна из консоли.")
            return true
        }

        sender as? Player?.let { player ->
            cooldowns[player.uniqueId]?.let { lastUsed ->
                val timeElapsed = System.currentTimeMillis() - lastUsed
                if (timeElapsed < cooldown * MILLIS_PER_SECOND) {
                    val timeLeft = (cooldown * MILLIS_PER_SECOND - timeElapsed) / MILLIS_PER_SECOND
                    player.sendMessage("§cПожалуйста, подождите $timeLeft секунд, прежде чем использовать эту команду снова.")
                    return true
                }
            }
            cooldowns[player.uniqueId] = System.currentTimeMillis()
        }

        if (permissionNode != null && !sender.hasPermission(permissionNode)) {
            sender.sendMessage("У вас нет разрешения использовать эту команду.")
            return true
        }

        if (args.isNotEmpty() && argsRequirement != null && args.size != argsRequirement) {
            sender.sendMessage(getUsage())
            return true
        }

        return onCommand(CommandExecution(sender, args))
    }

    abstract fun onCommand(execution: CommandExecution): Boolean

    override fun onTabComplete(sender: CommandSender, command: Command, alias: String, args: Array<out String>): List<String>? {
        return onTabComplete(CommandExecution(sender, args))
    }

    abstract fun onTabComplete(execution: CommandExecution): List<String>?

    class CommandExecution(val sender: CommandSender, val args: Array<out String>)
}
