package ru.airdead.modulessystem.global.minecraft

import org.bukkit.event.Listener
import org.bukkit.plugin.java.JavaPlugin
import ru.airdead.modulessystem.global.listeners.MovementListener
import ru.airdead.modulessystem.modules.ModulesManager
import ru.airdead.modulessystem.modules.PluginModule

abstract class ServerPlugin : JavaPlugin() {
    val modulesManager: ModulesManager = ModulesManager(this)
    abstract val components: List<Any>

    override fun onEnable() {
        instance = this
        server.pluginManager.registerEvents(MovementListener(), this)
        loadComponents()
    }

    override fun onDisable() {
        modulesManager.unloadAll()
    }

    private fun loadComponents() {
        components.forEach { component ->
            when (component) {
                is Listener -> register(component)
                is ServerCommand -> register(component)
                is PluginModule -> modulesManager.register(component)
            }
        }
        modulesManager.loadAll()
    }

    private fun register(command: ServerCommand) {
        val bukkitCommand = getCommand(command.name)
        if(bukkitCommand != null) {
            bukkitCommand.setExecutor(command)
            bukkitCommand.tabCompleter = command
        }
    }

    private fun register(listener: Listener) {
        server.pluginManager.registerEvents(listener, this)
    }

    companion object {
        lateinit var instance: ServerPlugin
            private set
    }
}