package carpet.mixins;

import carpet.CarpetSettings;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerGamePacketListenerImpl.class)
public abstract class ServerGamePacketListenerImpl_antiCheatDisabledMixin
{
    @Shadow private int aboveGroundTickCount;

    @Shadow private int aboveGroundVehicleTickCount;

    @Inject(method = "tick", at = @At("HEAD"))
    private void restrictFloatingBits(CallbackInfo ci)
    {
        if (CarpetSettings.antiCheatDisabled)
        {
            if (aboveGroundTickCount > 70) aboveGroundTickCount--;
            if (aboveGroundVehicleTickCount > 70) aboveGroundVehicleTickCount--;
        }

    }

    @WrapOperation(method = "handleMoveVehicle", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/server/network/ServerGamePacketListenerImpl;isSingleplayerOwner()Z"
    ))
    private boolean isServerTrusting(ServerGamePacketListenerImpl instance, Operation<Boolean> original)
    {
        return original.call(instance) || CarpetSettings.antiCheatDisabled;
    }

    @WrapOperation(method = "handleMovePlayer", require = 0, // don't crash with immersive portals,
             at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/server/level/ServerPlayer;isChangingDimension()Z"))
    private boolean relaxMoveRestrictions(ServerPlayer serverPlayerEntity, Operation<Boolean> original)
    {
        return CarpetSettings.antiCheatDisabled || original.call(serverPlayerEntity);
    }
}
