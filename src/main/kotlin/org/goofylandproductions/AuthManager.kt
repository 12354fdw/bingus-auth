package org.goofylandproductions

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import net.fabricmc.loader.api.FabricLoader
import java.nio.file.Path
import java.security.MessageDigest
import java.security.SecureRandom
import kotlin.io.path.*

data class AuthEntry (
    val hash: String,
    val salt: String
)

object AuthManager {
    private val gson = Gson()
    private val passwordStore = mutableMapOf<String, AuthEntry>()
    private lateinit var dataFile: Path

    fun init() {
        dataFile = FabricLoader.getInstance().configDir
            .resolve("bingus-auth").resolve("data.json")

        load()
    }

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

    private fun generateSalt(): ByteArray {
        val bytes = ByteArray(16)
        SecureRandom().nextBytes(bytes)
        return bytes
    }

    private fun hash(password: String, salt: ByteArray): ByteArray {
        val digest = MessageDigest.getInstance("SHA-256")
        digest.update(salt)
        digest.update(password.toByteArray(Charsets.UTF_8))
        return digest.digest()
    }

    private fun ByteArray.toHex(): String =
        joinToString("") { "%02X".format(it) }
}