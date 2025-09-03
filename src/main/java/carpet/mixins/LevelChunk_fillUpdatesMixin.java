package carpet.mixins;

import carpet.CarpetSettings;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = LevelChunk.class)
public class LevelChunk_fillUpdatesMixin
{
    // todo onStateReplaced needs a bit more love since it removes be which is needed
    @WrapOperation(method = "setBlockState", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/level/block/state/BlockState;onPlace(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;Z)V"
    ))
    private void onAdded(BlockState blockState, Level world_1, BlockPos blockPos_1, BlockState blockState_1, boolean boolean_1, Operation<Void> original)
    {
        if (!CarpetSettings.impendingFillSkipUpdates.get())
            original.call(blockState, world_1, blockPos_1, blockState_1, boolean_1);
    }

    @WrapOperation(method = "setBlockState", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/level/block/state/BlockState;onRemove(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;Z)V"
    ))
    private void onRemovedBlock(BlockState blockState, Level world, BlockPos pos, BlockState state, boolean moved, Operation<Void> original)
    {
        if (CarpetSettings.impendingFillSkipUpdates.get()) // doing due dilligence from AbstractBlock onStateReplaced
        {
            if (blockState.hasBlockEntity() && !blockState.is(state.getBlock()))
            {
                world.removeBlockEntity(pos);
            }
        }
        else
        {
            original.call(blockState, world, pos, state, moved);
        }
    }
}
