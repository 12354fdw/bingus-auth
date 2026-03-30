package org.goofylandproductions.commands

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.context.CommandContext
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands
import net.minecraft.network.chat.Component
import org.goofylandproductions.AuthManager

object RegisterCommand {
    fun registerCommand(dispatcher: CommandDispatcher<CommandSourceStack> ) {
        dispatcher.register(Commands.literal("register")
            .then(
                Commands.argument("password", StringArgumentType.word())
                    .executes { ctx ->
                        handleCommand(ctx)
                    }
            )
        )
    }

    private fun handleCommand(ctx: CommandContext<CommandSourceStack>): Int {
        val password = StringArgumentType.getString(ctx, "password")
        val player = ctx.source.player ?: return 0

        if (AuthManager.isRegistered(player.uuid)) {
            ctx.source.sendFailure(Component.literal("You are already registered!"))
            return 0
        }

        AuthManager.register(player.uuid, password)
        ctx.source.sendSuccess({ Component.literal("§aRegistered successfully! Logging in now!") }, false )

        AuthManager.authenticate(player, password)
        return 1
    }
}