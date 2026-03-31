package org.goofylandproductions

import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents
import net.minecraft.network.chat.Component
import net.minecraft.world.level.Level
import org.goofylandproductions.commands.DeletePasswordCommand
import org.goofylandproductions.commands.LoginCommand
import org.goofylandproductions.commands.RegisterCommand
import org.goofylandproductions.integrations.FloodgateHelper
import org.slf4j.LoggerFactory

object Bingusauth : ModInitializer {
	val logger = LoggerFactory.getLogger("bingus-auth")

	override fun onInitialize() {
		AuthManager.init()

		CommandRegistrationCallback.EVENT.register { dispatcher, _, _ ->
			RegisterCommand.registerCommand(dispatcher)
			LoginCommand.registerCommand(dispatcher)
			DeletePasswordCommand.registerCommand(dispatcher)
		}

		// save player hook
		ServerPlayConnectionEvents.JOIN.register { impl, sender, server ->
			val player = impl.player

			if (FloodgateHelper.isFloodgatePlayer(player.uuid)) {
				AuthManager.forceAuthenticate(player.uuid)
				return@register
			}

			player.sendSystemMessage(Component.literal("§cbingus-auth IS STILL IN §l§oBETA§r§c! please report bugs"))
			SavedLocationCache.savePlayerPos(player)
			player.teleportTo(
				server.getLevel(Level.OVERWORLD)!!,
				AuthConfig.spawnX, AuthConfig.spawnY, AuthConfig.spawnZ,
				setOf(),
				AuthConfig.spawnYaw, AuthConfig.spawnPitch,
				false
			)
			if (AuthManager.isRegistered(player.uuid)) {
				player.sendSystemMessage(Component.literal("§9Use /login or /l to login"))
			} else {
				player.sendSystemMessage(Component.literal("§9use /register to register"))
			}
		}

		ServerPlayConnectionEvents.DISCONNECT.register { impl, server ->
			AuthManager.onPlayerLeave(impl.player.uuid)
		}
	}
}