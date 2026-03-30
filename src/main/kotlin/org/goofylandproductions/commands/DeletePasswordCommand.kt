package org.goofylandproductions.commands

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.context.CommandContext
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands
import net.minecraft.network.chat.Component
import net.minecraft.server.permissions.Permissions
import org.goofylandproductions.AuthManager
import java.util.UUID

object DeletePasswordCommand {
    fun registerCommand(dispatcher: CommandDispatcher<CommandSourceStack> ) {
        dispatcher.register(Commands.literal("deletepassword")
            .requires { it.permissions().hasPermission(Permissions.COMMANDS_ADMIN) }
            .then(
                Commands.argument("playername", StringArgumentType.word())
                    .executes { ctx ->
                        handleCommand(ctx)
                    }
            )
        )
    }

    private fun handleCommand(ctx: CommandContext<CommandSourceStack>): Int {
        val username = StringArgumentType.getString(ctx, "playername")

        val onlinePlayer = ctx.source.server.playerList.getPlayerByName(username)
        val uuid = onlinePlayer?.uuid
            ?: UUID.nameUUIDFromBytes("OfflinePlayer:$username".toByteArray(Charsets.UTF_8))

        AuthManager.deletePasswordEntry(uuid)

        onlinePlayer?.connection?.disconnect(
            Component.literal("Your password was reset by an admin. Please reconnect and register again.")
        )

        ctx.source.sendSuccess({ Component.literal("Deleted password for $username") }, false)

        return 1
    }
}