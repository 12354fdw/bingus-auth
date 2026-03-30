package org.goofylandproductions.mixin;

import net.minecraft.network.protocol.game.*;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.goofylandproductions.AuthManager;

@Mixin(ServerGamePacketListenerImpl.class)
public abstract class AuthPacketListenerMixin {

    @Shadow
    public ServerPlayer player;

    private boolean isAuthed() {
        return AuthManager.INSTANCE.isAuthenticated(player.getUUID());
    }

    @Inject(method = "handleChat", at = @At("HEAD"), cancellable = true)
    private void onChat(ServerboundChatPacket packet, CallbackInfo ci) {
        if (!isAuthed()) ci.cancel();
    }

    @Inject(method = "handleChatCommand", at = @At("HEAD"), cancellable = true)
    private void onCommand(ServerboundChatCommandPacket packet, CallbackInfo ci) {
        if (!isAuthed()) {
            String command = packet.command().toLowerCase();
            if (!command.startsWith("login") && !command.startsWith("register") && !command.startsWith("l")) {
                ci.cancel();
            }
        }
    }

    @Inject(method = "handleMovePlayer", at = @At("HEAD"), cancellable = true)
    private void onMovePlayer(ServerboundMovePlayerPacket packet, CallbackInfo ci) {
        if (!isAuthed()) ci.cancel();
    }

    @Inject(method = "handlePlayerAction", at = @At("HEAD"), cancellable = true)
    private void onPlayerAction(ServerboundPlayerActionPacket packet, CallbackInfo ci) {
        if (!isAuthed()) ci.cancel();
    }

    @Inject(method = "handleInteract", at = @At("HEAD"), cancellable = true)
    private void onInteract(ServerboundInteractPacket packet, CallbackInfo ci) {
        if (!isAuthed()) ci.cancel();
    }

    @Inject(method = "handleUseItemOn", at = @At("HEAD"), cancellable = true)
    private void onUseItemOn(ServerboundUseItemOnPacket packet, CallbackInfo ci) {
        if (!isAuthed()) ci.cancel();
    }

    @Inject(method = "handleUseItem", at = @At("HEAD"), cancellable = true)
    private void onUseItem(ServerboundUseItemPacket packet, CallbackInfo ci) {
        if (!isAuthed()) ci.cancel();
    }

    @Inject(method = "handleContainerClick", at = @At("HEAD"), cancellable = true)
    private void onContainerClick(ServerboundContainerClickPacket packet, CallbackInfo ci) {
        if (!isAuthed()) ci.cancel();
    }
}
