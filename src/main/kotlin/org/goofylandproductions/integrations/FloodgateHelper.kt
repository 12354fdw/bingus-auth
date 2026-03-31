package org.goofylandproductions.integrations

import net.fabricmc.loader.api.FabricLoader
import org.goofylandproductions.Bingusauth
import java.util.UUID

object FloodgateHelper {
    private val isLoaded = FabricLoader.getInstance().isModLoaded("floodgate")

    fun isFloodgatePlayer(uuid: UUID): Boolean {

        // TODO: add proper check when floodgate updates to 26.1
        if (uuid.mostSignificantBits == 0L) return true

        return false
    }
}