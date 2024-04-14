package ru.airdead.modulessystem.global.listeners

import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.event.Cancellable
import org.bukkit.event.HandlerList
import org.bukkit.event.player.PlayerEvent

class CustomPlayerMoveEvent(
    player: Player,
    var from: Location,
    var to: Location?
) : PlayerEvent(player), Cancellable {
    private var cancelled = false

    override fun isCancelled(): Boolean = cancelled
    override fun setCancelled(cancel: Boolean) {
        cancelled = cancel
    }

    fun hasChangedPosition(): Boolean {
        val safeTo = to
        return safeTo != null && !from.equals(safeTo)
    }

    fun hasChangedBlock(): Boolean {
        val safeTo = to
        return safeTo != null && (from.blockX != safeTo.blockX || from.blockY != safeTo.blockY || from.blockZ != safeTo.blockZ)
    }

    fun hasChangedOrientation(): Boolean {
        val safeTo = to
        return safeTo != null && (from.pitch != safeTo.pitch || from.yaw != safeTo.yaw)
    }

    companion object {
        private val internalHandlerList = HandlerList()
        @JvmStatic fun getHandlerList(): HandlerList = internalHandlerList
    }
    override fun getHandlers(): HandlerList = Companion.getHandlerList()
}
