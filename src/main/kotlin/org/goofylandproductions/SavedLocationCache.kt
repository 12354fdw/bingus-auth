package org.goofylandproductions

import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceKey
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.level.Level
import java.util.UUID

data class SavedLocation(
    val x: Double,
    val y: Double,
    val z: Double,
    val yaw: Float,
    val pitch: Float,
    val dimension: ResourceKey<Level>
) {
    constructor(player: ServerPlayer) : this(
        x = player.x,
        y = player.y,
        z = player.z,
        yaw = player.yRot,
        pitch = player.xRot,
        dimension = player.level().dimension()
    )
}

// only store player data in RAM, so you gotta do ram scraping to extract such sensitive data
object SavedLocationCache {
    private val locations = mutableMapOf<UUID, SavedLocation>()
    private val safeTeleportPlayers = mutableSetOf<UUID>()

    fun isSafeTeleport(uuid: UUID): Boolean = safeTeleportPlayers.contains(uuid)

    fun savePlayerPos(player: ServerPlayer) {
        if (!locations.contains(player.uuid)) locations[player.uuid] = SavedLocation(player)
        safeTeleportPlayers.add(player.uuid)
    }

    fun teleportPlayer(player: ServerPlayer) {
        val entry = requireNotNull(locations[player.uuid]) {
            player.sendSystemMessage(Component.literal("§cNo saved location was found! THIS IS A BUG"))
            "cannot teleport \${player.name}, no save location was found!"
        }

        val targetLevel = requireNotNull(player.level().server.getLevel(entry.dimension)) {
            player.sendSystemMessage(Component.literal("§saved location contains a dimension that doesn't exists! THIS IS MAYBE A BUG"))
            "cannot teleport \${player.name}, \${entry.dimension.toString()} wasn't found!"
        }
        player.teleportTo(
            targetLevel,
            entry.x, entry.y, entry.z, mutableSetOf(),
            entry.yaw, entry.pitch, true
        )

    }

    fun authenticated(player: ServerPlayer) {
        teleportPlayer(player)
        safeTeleportPlayers.remove(player.uuid)
        locations.remove(player.uuid)
    }
}