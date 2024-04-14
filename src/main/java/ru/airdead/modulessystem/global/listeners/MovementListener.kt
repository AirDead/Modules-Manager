package ru.airdead.modulessystem.global.listeners

import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerMoveEvent
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicLong

class MovementListener : Listener {
    private val initialLocations = ConcurrentHashMap<UUID, Location>()
    private val lastEventTimes = ConcurrentHashMap<UUID, AtomicLong>()
    private val TICKS_PER_EVENT = 250L  // 5 seconds, assuming 50 ticks per second

    @EventHandler
    fun onPlayerMove(event: PlayerMoveEvent) {
        val player = event.player
        val playerUUID = player.uniqueId
        val currentTime = System.currentTimeMillis()
        val finalTo = event.to  // Create a local constant for the 'to' location

        initialLocations.putIfAbsent(playerUUID, event.from)
        val lastTime = lastEventTimes.computeIfAbsent(playerUUID) { AtomicLong(currentTime) }

        if (currentTime - lastTime.get() >= TICKS_PER_EVENT && finalTo != null && hasMoved(event.from, finalTo)) {
            lastTime.set(currentTime)
            callCustomEvent(player, event.from, finalTo)
        }
    }

    private fun callCustomEvent(player: Player, from: Location, to: Location) {
        val customEvent = CustomPlayerMoveEvent(player, from, to)
        player.server.pluginManager.callEvent(customEvent)
        if (customEvent.isCancelled) {
            player.teleport(initialLocations[player.uniqueId]!!)
        }
    }

    private fun hasMoved(from: Location, to: Location): Boolean =
        from.blockX != to.blockX || from.blockY != to.blockY || from.blockZ != to.blockZ ||
                from.yaw != to.yaw || from.pitch != to.pitch
}
