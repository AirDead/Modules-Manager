@file:Suppress("NOTHING_TO_INLINE")

package ru.airdead.modulessystem.global.minecraft

import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.OfflinePlayer

class CommandExecution(val player: CommandSender, val args: Array<out String>) {

    inline fun throwUsage(): Nothing {
        throw ServerCommand.ThrowUsage()
    }


    fun getPlayer(index: Int): Player {
        val name = args[index]
        if (name.isEmpty() || name.isBlank()) throwUsage()
        return Bukkit.getPlayer(name) ?: throwUsage()
    }

    fun getInt(index: Int): Int {
        val value = args[index]
        if (value.isEmpty() || value.isBlank()) throwUsage()
        return value.toIntOrNull() ?: throwUsage()
    }

    fun getIntOrNull(index: Int): Int? = args[index].toIntOrNull()

    fun getDouble(index: Int): Double {
        val value = args[index]
        if (value.isEmpty() || value.isBlank()) throwUsage()
        return value.toDoubleOrNull() ?: throwUsage()
    }

    fun getDoubleOrNull(index: Int): Double? = args[index].toDoubleOrNull()

    fun getFloat(index: Int): Float {
        val value = args[index]
        if (value.isEmpty() || value.isBlank()) throwUsage()
        return value.toFloatOrNull() ?: throwUsage()
    }

    fun getFloatOrNull(index: Int): Float? = args[index].toFloatOrNull()

    fun getLong(index: Int): Long {
        val value = args[index]
        if (value.isEmpty() || value.isBlank()) throwUsage()
        return value.toLongOrNull() ?: throwUsage()
    }

    fun getLongOrNull(index: Int): Long? = args[index].toLongOrNull()

    fun getBoolean(index: Int): Boolean {
        val value = args[index]
        if (value.isEmpty() || value.isBlank()) throwUsage()
        return when (value.lowercase()) {
            "true", "yes", "1" -> true
            "false", "no", "0" -> false
            else -> throwUsage()
        }
    }

    fun getBooleanOrNull(index: Int): Boolean? = when (args[index].lowercase()) {
        "true", "yes", "1" -> true
        "false", "no", "0" -> false
        else -> null
    }

    fun getByte(index: Int): Byte {
        val value = args[index]
        if (value.isEmpty() || value.isBlank()) throwUsage()
        return value.toByteOrNull() ?: throwUsage()
    }

    fun getByteOrNull(index: Int): Byte? = args[index].toByteOrNull()

    fun getShort(index: Int): Short {
        val value = args[index]
        if (value.isEmpty() || value.isBlank()) throwUsage()
        return value.toShortOrNull() ?: throwUsage()
    }

    fun getShortOrNull(index: Int): Short? = args[index].toShortOrNull()

    fun getChar(index: Int): Char {
        val value = args[index]
        if (value.isEmpty() || value.isBlank()) throwUsage()
        return value.firstOrNull() ?: throwUsage()
    }

    fun getCharOrNull(index: Int): Char? = args.getOrNull(index)?.firstOrNull()

    fun getOfflinePlayer(index: Int): OfflinePlayer {
        val name = args[index]
        if (name.isEmpty() || name.isBlank()) throwUsage()
        return Bukkit.getOfflinePlayer(name)
    }

    fun getOnlinePlayer(index: Int): Player = getPlayer(index)
}
