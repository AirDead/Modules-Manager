package ru.airdead.modulessystem.global.minecraft

import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabExecutor
import org.bukkit.entity.Player
import java.util.*

abstract class ServerCommand : TabExecutor {

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

        if (sender is Player) {
            cooldowns[sender.uniqueId]?.let { lastUsed ->
                val timeElapsed = System.currentTimeMillis() - lastUsed
                if (timeElapsed < cooldown * MILLIS_PER_SECOND) {
                    val timeLeft = (cooldown * MILLIS_PER_SECOND - timeElapsed) / MILLIS_PER_SECOND
                    sender.sendMessage("§cПожалуйста, подождите $timeLeft секунд, прежде чем использовать эту команду снова.")
                    return true
                }
            }
            cooldowns[sender.uniqueId] = System.currentTimeMillis()
        }

        val permNode = permissionNode
        if (permNode != null && !sender.hasPermission(permNode)) {
            sender.sendMessage("У вас нет разрешения использовать эту команду.")
            return true
        }

        val requiredArgs = argsRequirement
        if (args.isNotEmpty() && requiredArgs != null && args.size < requiredArgs) {
            sender.sendMessage(getUsage())
            return true
        }

        if (!isConsoleFriendly) {
            return onCommand(CommandExecution(sender as Player, args))
        } else {
            return onCommand(CommandExecution(sender, args))
        }
    }

    abstract fun onCommand(execution: CommandExecution): Boolean

    override fun onTabComplete(sender: CommandSender, command: Command, alias: String, args: Array<out String>): List<String> {
        return onTabComplete(CommandExecution(sender, args))
    }

    abstract fun onTabComplete(execution: CommandExecution): List<String>

    class ThrowUsage : RuntimeException()
}
