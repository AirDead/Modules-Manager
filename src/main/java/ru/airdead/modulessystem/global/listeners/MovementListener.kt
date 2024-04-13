package ru.airdead.modulessystem.global.listeners

import java.util.UUID
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerMoveEvent
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicLong

class MovementListener : Listener {
    private val lastEventTimes: MutableMap<UUID, AtomicLong> = ConcurrentHashMap()
    private val TICKS_PER_EVENT = 5 * 50L // Преобразуем тики в миллисекунды заранее

    @EventHandler
    fun onPlayerMove(event: PlayerMoveEvent) {
        val player: Player = event.player
        val playerUUID: UUID = player.uniqueId
        val currentTime = System.currentTimeMillis()

        val lastTime = lastEventTimes.computeIfAbsent(playerUUID) { AtomicLong(currentTime) }

        if (currentTime - lastTime.get() >= TICKS_PER_EVENT) {
            lastTime.set(currentTime)
            if (hasMoved(event.from, event.to)) {
                callCustomEvent(player, event.from, event.to)
            }
        }
    }

    private fun callCustomEvent(player: Player, from: Location, to: Location?) {
        val customEvent = CustomPlayerMoveEvent(player, from, to)
        player.server.pluginManager.callEvent(customEvent)
    }

    private fun hasMoved(from: Location, to: Location?): Boolean {
        if (to == null) return false
        return from.blockX != to.blockX || from.blockY != to.blockY || from.blockZ != to.blockZ
    }
}
