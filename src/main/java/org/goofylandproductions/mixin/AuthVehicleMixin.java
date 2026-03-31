package org.goofylandproductions.mixin;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.storage.ValueInput;
import org.goofylandproductions.SavedLocationCache;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.UUID;

@Mixin(ServerPlayer.class)
public abstract class AuthVehicleMixin {

    @Inject(method = "readAdditionalSaveData", at = @At("HEAD"))
    private void onReadSaveData(ValueInput input, CallbackInfo ci) {
        ServerPlayer player = (ServerPlayer) (Object) this;
        input.child("RootVehicle").ifPresent(rootVehicle ->
            rootVehicle.getIntArray("Attach").ifPresent(ints -> {
                // why?????
                if (ints.length == 4) {
                    UUID vehicleUUID = new UUID(
                        (long) ints[0] << 32 | (ints[1] & 0xFFFFFFFFL),
                        (long) ints[2] << 32 | (ints[3] & 0xFFFFFFFFL)
                    );
                    SavedLocationCache.INSTANCE.saveVehicleUUID(player.getUUID(), vehicleUUID);
                }
            })
        );
    }
}
