package ru.airdead.modulessystem.global.listeners

import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.event.Cancellable
import org.bukkit.event.HandlerList
import org.bukkit.event.player.PlayerEvent
import org.jetbrains.annotations.NotNull

class CustomPlayerMoveEvent(
    player: Player,
    @NotNull var from: Location,
    @NotNull var to: Location?
) : PlayerEvent(player), Cancellable {
    private var cancelled = false

    companion object {
        @JvmStatic
        val handlerList = HandlerList()  // Renamed to avoid potential clashing

        @JvmStatic
        fun getHandlerList(): HandlerList = handlerList
    }

    override fun getHandlers(): HandlerList = handlerList

    override fun isCancelled(): Boolean = cancelled

    override fun setCancelled(cancel: Boolean) {
        this.cancelled = cancel
    }

    fun hasChangedPosition(): Boolean = !from.equals(to)

    fun hasChangedBlock(): Boolean =
        from.blockX != to!!.blockX || from.blockY != to!!.blockY || from.blockZ != to!!.blockZ

    fun hasChangedOrientation(): Boolean =
        from.pitch != to!!.pitch || from.yaw != to!!.yaw
}
