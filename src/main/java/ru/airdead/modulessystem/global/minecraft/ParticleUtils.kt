package ru.airdead.modulessystem.global.minecraft

import org.bukkit.Location
import org.bukkit.Particle
import org.bukkit.World
import org.bukkit.entity.Player
import org.bukkit.plugin.Plugin
import org.bukkit.scheduler.BukkitRunnable
import org.bukkit.util.Vector

fun spawnCircleParticles(center: Location, radius: Double, particleCount: Int, particleType: Particle) {
    val world = center.world
    for (i in 0 until particleCount) {
        val angle = 2 * Math.PI * i / particleCount
        val x = center.x + radius * Math.cos(angle)
        val z = center.z + radius * Math.sin(angle)
        val particleLocation = Location(world, x, center.y, z)
        world?.spawnParticle(particleType, particleLocation, 1)
    }
}

fun spawnParticlesUntilBlockOrPlayer(start: Location, direction: Vector, maxDistance: Double, particleType: Particle, particleCount: Int): Player? {
    var currentLocation = start.clone()
    val step = maxDistance / particleCount
    for (i in 0 until particleCount) {
        currentLocation.add(direction.normalize().multiply(step))
        start.world?.spawnParticle(particleType, currentLocation, 1)
        if (currentLocation.block.type.isSolid) {
            return null
        }
        start.world?.players?.forEach { player ->
            if (player.location.distance(currentLocation) < 2) {
                return player
            }
        }
    }
    return null
}

fun spawnShrinkingCircle(plugin: Plugin, player: Player, initialRadius: Double, particleType: Particle = Particle.FLAME, durationTicks: Long = 100L, steps: Int = 20, particleCount: Int = 36) {
    val world = player.world
    val initialLocation = player.location

    object : BukkitRunnable() {
        var currentRadius = initialRadius
        var step = 0

        override fun run() {
            if (currentRadius <= 0 || step >= steps) {
                this.cancel()
            } else {
                currentRadius -= initialRadius / steps
                step++
                val angleIncrement = 360.0 / particleCount
                for (i in 0 until particleCount) {
                    val radians = Math.toRadians(i * angleIncrement)
                    val x = initialLocation.x + currentRadius * Math.cos(radians)
                    val z = initialLocation.z + currentRadius * Math.sin(radians)
                    val particleLocation = Location(world, x, initialLocation.y, z)
                    world.spawnParticle(particleType, particleLocation, 1)
                }
            }
        }
    }.runTaskTimer(plugin, 0L, durationTicks / steps)
}

fun spawnParticleCube(corner1: Location, corner2: Location, particleType: Particle = Particle.FLAME, particleCount: Int) {
    val world: World = corner1.world
    if (corner1.world != corner2.world) {
        throw IllegalArgumentException("Locations must be in the same world")
    }

    val minX = minOf(corner1.x, corner2.x).toInt()
    val maxX = maxOf(corner1.x, corner2.x).toInt()
    val minY = minOf(corner1.y, corner2.y).toInt()
    val maxY = maxOf(corner1.y, corner2.y).toInt()
    val minZ = minOf(corner1.z, corner2.z).toInt()
    val maxZ = maxOf(corner1.z, corner2.z).toInt()

    for (x in minX..maxX step ((maxX - minX) / particleCount + 1)) {
        for (z in minZ..maxZ step ((maxZ - minZ) / particleCount + 1)) {
            world.spawnParticle(particleType, Location(world, x.toDouble(), minY.toDouble(), z.toDouble()), 1)
            world.spawnParticle(particleType, Location(world, x.toDouble(), maxY.toDouble(), z.toDouble()), 1)
        }
    }

    for (y in minY..maxY step ((maxY - minY) / particleCount + 1)) {
        for (x in minX..maxX step ((maxX - minX) / particleCount + 1)) {
            world.spawnParticle(particleType, Location(world, x.toDouble(), y.toDouble(), minZ.toDouble()), 1)
            world.spawnParticle(particleType, Location(world, x.toDouble(), y.toDouble(), maxZ.toDouble()), 1)
        }
    }

    for (y in minY..maxY step ((maxY - minY) / particleCount + 1)) {
        for (z in minZ..maxZ step ((maxZ - minZ) / particleCount + 1)) {
            world.spawnParticle(particleType, Location(world, minX.toDouble(), y.toDouble(), z.toDouble()), 1)
            world.spawnParticle(particleType, Location(world, maxX.toDouble(), y.toDouble(), z.toDouble()), 1)
        }
    }
}

fun spawnFlyingParticles(pathStart: Location, direction: Vector, length: Double, speed: Long, particleType: Particle, particleCount: Int, plugin: Plugin) {
    object : BukkitRunnable() {
        var currentLocation = pathStart.clone()
        val step = direction.normalize().multiply(length / particleCount)

        override fun run() {
            val previousLocation = currentLocation.clone()
            currentLocation.add(step)
            pathStart.world?.spawnParticle(particleType, currentLocation, 1)
            pathStart.world?.spawnParticle(particleType, previousLocation, 0) // Remove previous particle
            if (currentLocation.distance(pathStart) >= length) {
                this.cancel()
            }
        }
    }.runTaskTimer(plugin, 0L, speed)
}
