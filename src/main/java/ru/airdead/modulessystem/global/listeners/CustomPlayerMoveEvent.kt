package ru.airdead.modulessystem.global.listeners

import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.event.HandlerList
import org.bukkit.event.player.PlayerEvent
import org.bukkit.event.Cancellable

class CustomPlayerMoveEvent(
    player: Player,
    val from: Location,
    val to: Location
) : PlayerEvent(player), Cancellable {
    private var cancelled: Boolean = false

    override fun isCancelled(): Boolean = cancelled

    override fun setCancelled(cancel: Boolean) {
        cancelled = cancel
    }

    companion object {
        @JvmStatic
        private val handlerList = HandlerList()

        @JvmStatic
        fun getHandlerList(): HandlerList = handlerList
    }

    override fun getHandlers(): HandlerList = handlerList
}
