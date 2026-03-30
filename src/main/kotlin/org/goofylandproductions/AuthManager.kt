package org.goofylandproductions

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import net.fabricmc.loader.api.FabricLoader
import java.nio.file.Path
import java.util.UUID
import kotlin.io.path.*
import de.mkammerer.argon2.Argon2Factory
import net.minecraft.server.level.ServerPlayer

data class AuthEntry (
    val hash: String,
)

object AuthManager {
    private val gson = Gson()
    private val passwordStore = mutableMapOf<String, AuthEntry>()
    private val authenticated = mutableSetOf<UUID>()
    private lateinit var dataFile: Path

    private val argon2 = Argon2Factory.create()

    fun init() {
        dataFile = FabricLoader.getInstance().configDir
            .resolve("bingus-auth").resolve("data.json")
        load()
    }

    // login & registration

    fun isRegistered(uuid: UUID): Boolean =
        passwordStore.containsKey(uuid.toString())

    fun register(uuid: UUID, password: String) {
        if (passwordStore.containsKey(uuid.toString())) return

        val hash = argon2.hash(4,131072,2,password.toCharArray())

        passwordStore[uuid.toString()] = AuthEntry(hash)
        save()
    }

    private fun checkPassword(uuid: UUID, password: String): Boolean {
        val entry = passwordStore[uuid.toString()] ?: return false
        return argon2.verify(entry.hash, password.toCharArray())
    }

    fun deletePasswordEntry(uuid: UUID) {
        passwordStore.remove(uuid.toString())
        authenticated.remove(uuid)
        save()
    }

    // auth

    fun isAuthenticated(uuid: UUID): Boolean = authenticated.contains(uuid)

    fun authenticate(player: ServerPlayer, password: String): Boolean {
        if (!checkPassword(player.uuid, password)) return false
        authenticated.add(player.uuid)
        SavedLocationCache.authenticated(player)
        return true
    }
    fun onPlayerLeave(uuid: UUID) {
        authenticated.remove(uuid)
    }

    // IO helpers

    private fun save() {
        dataFile.parent.createDirectories()
        dataFile.writeText(gson.toJson(passwordStore))
    }

    private fun load() {
        if (!dataFile.exists()) return
        val type = object : TypeToken<Map<String, AuthEntry>>() {}.type
        val loaded: Map<String, AuthEntry> = gson.fromJson(dataFile.readText(), type)
        passwordStore.putAll(loaded)
    }

    // crypto helpers

    private fun ByteArray.toHex(): String =
        joinToString("") { "%02X".format(it) }

    private fun String.fromHex(): ByteArray =
        chunked(2).map { it.toInt(16).toByte() }.toByteArray()
}