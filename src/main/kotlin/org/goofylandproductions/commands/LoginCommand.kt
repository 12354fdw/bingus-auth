package org.goofylandproductions.commands

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.context.CommandContext
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands
import net.minecraft.network.chat.Component
import org.goofylandproductions.AuthManager

object LoginCommand {
    fun registerCommand(dispatcher: CommandDispatcher<CommandSourceStack>) {
        dispatcher.register(
            Commands.literal("login")
                .then(
                    Commands.argument("password", StringArgumentType.word())
                        .executes { ctx ->
                            handleCommand(ctx)
                        }
                )
        )

        dispatcher.register(
            Commands.literal("l")
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

        if (!AuthManager.isRegistered(player.uuid)) {
            ctx.source.sendFailure(Component.literal("You are not registered! Use /register first."))
            return 0
        }

        if (!AuthManager.authenticate(player, password)) {
            ctx.source.sendFailure(Component.literal("Wrong password!"))
            return 0
        }
        ctx.source.sendSuccess({ Component.literal("Logged in successfully!") }, false)

        return 1
    }
}
