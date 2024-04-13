package ru.airdead.modulessystem.modules

import ru.airdead.modulessystem.global.minecraft.ServerPlugin

interface PluginModule {
    val id: String
    fun onLoad(plugin: ServerPlugin) {}
    fun onUnload(plugin: ServerPlugin) {}

    /**
     * The priority of the module when loading.
     *
     * The higher the number, the higher the priority.
     */
    val loadPriority: Int
        get() = 0

    /**
     * The priority of the module when unloading.
     *
     * The higher the number, the higher the priority.
     */
    val unloadPriority: Int
        get() = 0
}
