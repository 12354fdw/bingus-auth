package org.goofylandproductions.mixin;

import net.minecraft.server.level.ServerPlayer;
import org.goofylandproductions.SavedLocationCache;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.goofylandproductions.AuthManager;
import org.goofylandproductions.AuthConfig;
import net.minecraft.world.level.Level;

@Mixin(ServerPlayer.class)
public abstract class AuthPlayerTickMixin {

    @Inject(method = "tick", at = @At("HEAD"))
    private void onTick(CallbackInfo ci) {
        ServerPlayer player = (ServerPlayer) (Object) this;
        if (!AuthManager.INSTANCE.isAuthenticated(player.getUUID()) && SavedLocationCache.INSTANCE.isSafeTeleport(player.getUUID())) {
            player.teleportTo(
                player.level().getServer().getLevel(Level.OVERWORLD),
                AuthConfig.INSTANCE.getSpawnX(),
                AuthConfig.INSTANCE.getSpawnY(),
                AuthConfig.INSTANCE.getSpawnZ(),
                java.util.Set.of(),
                AuthConfig.INSTANCE.getSpawnYaw(),
                AuthConfig.INSTANCE.getSpawnPitch(),
                false
            );
        }
    }
}
