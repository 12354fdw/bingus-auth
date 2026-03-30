package org.goofylandproductions

import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback
import org.goofylandproductions.commands.LoginCommand
import org.goofylandproductions.commands.RegisterCommand
import org.slf4j.LoggerFactory

object Bingusauth : ModInitializer {
    private val logger = LoggerFactory.getLogger("bingus-auth")

	override fun onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.

		//logger.info("Hello Fabric world!")

		CommandRegistrationCallback.EVENT.register { dispatcher, context, selection ->
			RegisterCommand.registerCommand(dispatcher)
			LoginCommand.registerCommand(dispatcher)
		}
	}
}