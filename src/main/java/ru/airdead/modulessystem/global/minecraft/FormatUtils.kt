package ru.airdead.modulessystem.global.minecraft

import org.bukkit.ChatColor
import java.lang.reflect.Field
import java.util.regex.Pattern

enum class FormatType {
    CLASS
}

fun String.formatPlaceholders(type: FormatType, map: Map<String, Any>): String = when (type) {
    FormatType.CLASS -> replaceClassPlaceholders(map)
}

private val hexColorPattern by lazy { Pattern.compile("#[a-fA-F0-9]{6}") }

fun String.replaceHexColorPlaceholders(): String {
    val matcher = hexColorPattern.matcher(this)
    val result = StringBuffer()
    while (matcher.find()) {
        val hexColor = matcher.group()
        val translated = hexColor.drop(1).fold("&") { acc, char -> "$acc&$char" }
        matcher.appendReplacement(result, translated)
    }
    matcher.appendTail(result)
    return ChatColor.translateAlternateColorCodes('&', result.toString())
}

fun String.colorize(char: Char = '&'): String = ChatColor.translateAlternateColorCodes(char, this)

private fun String.replaceClassPlaceholders(map: Map<String, Any>): String {
    val pattern = Regex("\\{([\\w.]+)\\}")
    return pattern.replace(this) { matchResult ->
        val fullPath = matchResult.groupValues[1]
        val pathParts = fullPath.split(".")
        val className = pathParts.first()
        map[className]?.let { instance ->
            safelyGetValue(instance, pathParts.drop(1)).toString() ?: "error"
        } ?: "undefined"
    }
}

private fun safelyGetValue(instance: Any?, pathParts: List<String>): Any? {
    return try {
        pathParts.fold(instance) { currentInstance, part ->
            currentInstance?.getFieldValue(part)
        }
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

private fun Any?.getFieldValue(fieldName: String): Any? {
    if (this == null) return null
    return try {
        val field: Field = this::class.java.getDeclaredField(fieldName)
        field.isAccessible = true
        field.get(this)?.toString()
    } catch (e: Exception) {
        println("Error accessing field $fieldName: ${e.message}")
        null
    }
}
