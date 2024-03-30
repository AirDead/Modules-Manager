package ru.airdead.modulessystem.global.minecraft

import org.bukkit.event.Listener
import org.bukkit.plugin.java.JavaPlugin
import ru.airdead.modulessystem.modules.ModulesManager
import ru.airdead.modulessystem.modules.PluginModule

abstract class ServerPlugin : JavaPlugin() {

    val modulesManager: ModulesManager = ModulesManager(this)

    abstract val components: List<Any>



    override fun onEnable() {
        instance = this
        loadComponents()
    }

    override fun onDisable() {
        modulesManager.unloadAll()
    }

    fun loadComponents() {
        components.forEach {
            if (it is Listener) {
                register(it)
            }
            if (it is ServerCommand) {
                register(it)
            }
            if (it is PluginModule) {
                modulesManager.register(it)
            }
        }

        modulesManager.loadAll()
    }




    fun register(command: ServerCommand) {
        val bukkitCommand = getCommand(command.name)
        if(bukkitCommand != null) {
            bukkitCommand.setExecutor(command)
            bukkitCommand.tabCompleter = command
        }
    }
    fun register(listener: Listener) {
        server.pluginManager.registerEvents(listener, this)
    }

    companion object {
        lateinit var instance: ServerPlugin
            private set
    }


}