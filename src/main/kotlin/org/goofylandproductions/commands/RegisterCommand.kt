package org.goofylandproductions.commands

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.context.CommandContext
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands

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
        val player = ctx.source.player

        // TODO: add logic

        return 1
    }
}