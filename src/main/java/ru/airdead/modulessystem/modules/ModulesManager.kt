package ru.airdead.modulessystem.modules



import ru.airdead.modulessystem.global.minecraft.TaskMoment
import ru.airdead.modulessystem.global.map.ListsMap
import ru.airdead.modulessystem.global.map.Snowflake
import ru.airdead.modulessystem.global.minecraft.ServerPlugin


class ModulesManager(val plugin: ServerPlugin) {
    val modules = LinkedHashMap<String, PluginModule>()

    var isLoading = false
    var isUnloading = false
    fun getModule(moduleName: String): PluginModule? {
        return modules[moduleName]
    }

    fun register(vararg modules: PluginModule) {
        modules.forEach {
            this.modules[it.id] = it
        }
    }

    fun unregister(moduleName: String) {
        val module = getModule(moduleName)
        if (module != null) {
            unregister(module)
        }
    }

    fun unregister(module: PluginModule) {
        modules.remove(module.id)
    }

    fun loadAll() {
        val modules = modules.values.toList().sortedByDescending { it.loadPriority }
        isLoading = true
        modules.forEach {
            updateState(it.id, State.LOADING)
            executeTasks(TaskMoment.BEFORE_LOAD, it.id)
            try {
                it.onLoad(plugin)
            } catch (e: Throwable) {
                plugin.logger.severe("Error while loading module '${it.id}'!")
                e.printStackTrace()
            }
            updateState(it.id, State.LOADED)
            executeTasks(TaskMoment.AFTER_LOAD, it.id)
        }
        isLoading = false
    }

    fun unloadAll() {
        val modules = modules.values.toList().sortedByDescending { it.unloadPriority }
        isUnloading = true
        modules.forEach {
            updateState(it.id, State.UNLOADING)
            executeTasks(TaskMoment.BEFORE_UNLOAD, it.id)
            try {
                it.onUnload(plugin)
            } catch (e: Exception) {
                plugin.logger.severe("Error while unloading module '${it.id}'!")
                e.printStackTrace()
            }
            updateState(it.id, State.UNLOADED)
            executeTasks(TaskMoment.AFTER_UNLOAD, it.id)
        }
        isUnloading = false
    }


    data class TaskData(
        val moment: TaskMoment,
        override val id: String,
        val moduleId: String,
        val task: () -> Unit
    ) : Snowflake<String>

    val tasks = ListsMap<TaskMoment, TaskData>()
    fun addTask(moment: TaskMoment, taskId: String, moduleId: String, task: () -> Unit) {
        tasks.add(moment, TaskData(moment, taskId, moduleId, task))
        if (moment == TaskMoment.AFTER_LOAD && isLoaded(moduleId)) {
            task()
        }
    }

    private fun executeTasks(moment: TaskMoment, moduleId: String) {
        val tasks = tasks[moment]?.filter { it.moduleId == moduleId }
        tasks?.forEach {
            try {
                it.task()
            } catch (e: Throwable) {
                plugin.logger.severe("Error while executing task '${it.id}'!")
                e.printStackTrace()
            }
        }
    }




    val modulesState = HashMap<String, State>()

    fun getState(moduleName: String): State {
        return modulesState[moduleName] ?: State.UNLOADED
    }

    fun isLoaded(moduleName: String) = getState(moduleName) == State.LOADED
    fun isLoading(moduleName: String) = getState(moduleName) == State.LOADING
    fun isUnloading(moduleName: String) = getState(moduleName) == State.UNLOADING
    fun isUnloaded(moduleName: String) = getState(moduleName) == State.UNLOADED



    private fun updateState(moduleName: String, state: State) {
        modulesState[moduleName] = state
    }


    fun reloadAll() {
        unloadAll()
        loadAll()
    }

    enum class State {
        LOADING,
        LOADED,
        UNLOADED,
        UNLOADING
    }
}
