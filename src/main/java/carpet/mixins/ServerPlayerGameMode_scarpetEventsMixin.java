package carpet.mixins;

import carpet.fakes.ServerPlayerInteractionManagerInterface;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerPlayerGameMode;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static carpet.script.CarpetEventServer.Event.PLAYER_BREAK_BLOCK;
import static carpet.script.CarpetEventServer.Event.PLAYER_INTERACTS_WITH_BLOCK;


@Mixin(ServerPlayerGameMode.class)
public class ServerPlayerGameMode_scarpetEventsMixin implements ServerPlayerInteractionManagerInterface
{
    @Final @Shadow protected ServerPlayer player;

    @Shadow private boolean isDestroyingBlock;

    @Shadow private BlockPos destroyPos;

    @Shadow private int lastSentState;

    @Shadow protected ServerLevel level;

    @WrapOperation(
            method = "destroyBlock", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/server/level/ServerPlayerGameMode;removeBlock(Lnet/minecraft/core/BlockPos;Z)Z",
            remap = false
    ))
    private boolean onBlockBroken(ServerPlayerGameMode instance, BlockPos blockPos, boolean canHarvest, Operation<Boolean> original, @Local BlockState blockState)
    {
        if(PLAYER_BREAK_BLOCK.onBlockBroken(player, blockPos, blockState)) {
            this.level.sendBlockUpdated(blockPos, blockState, blockState, 3);
            return false;
        }
        return original.call(instance, blockPos, canHarvest);
    }

    @Inject(method = "useItemOn", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/advancements/critereon/ItemUsedOnLocationTrigger;trigger(Lnet/minecraft/server/level/ServerPlayer;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/item/ItemStack;)V",
            shift = At.Shift.BEFORE
    ))
    private void onBlockActivated(ServerPlayer serverPlayerEntity, Level world, ItemStack stack, InteractionHand hand, BlockHitResult hitResult, CallbackInfoReturnable<InteractionResult> cir)
    {
        PLAYER_INTERACTS_WITH_BLOCK.onBlockHit(player, hand, hitResult);
    }

    @Override
    public BlockPos getCurrentBreakingBlock()
    {
        if (!isDestroyingBlock) return null;
        return destroyPos;
    }

    @Override
    public int getCurrentBlockBreakingProgress()
    {
        if (!isDestroyingBlock) return -1;
        return lastSentState;
    }

    @Override
    public void setBlockBreakingProgress(int progress)
    {
        lastSentState = Mth.clamp(progress, -1, 10);
        level.destroyBlockProgress(-1*this.player.getId(), destroyPos, lastSentState);
    }
}
