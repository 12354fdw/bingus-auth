package org.goofylandproductions

import net.minecraft.server.level.ServerPlayer
import java.util.UUID

data class SavedLocation(
    val x: Double,
    val y: Double,
    val z: Double,
    val yaw: Float,
    val pitch: Float,
    val dimension: String
)

object SavedLocationCache {
    private val locations = mutableMapOf<String, SavedLocation>()
    private val safeTeleportPlayers = mutableSetOf<UUID>()

    fun safeTeleport(uuid: UUID): Boolean = safeTeleportPlayers.contains(uuid)

    fun savePlayerPos(player: ServerPlayer) {
        safeTeleportPlayers.add(player.uuid)
    }

    fun removeSafeTeleport(uuid: UUID) {
        safeTeleportPlayers.remove(uuid)
    }
}