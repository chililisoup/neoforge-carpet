package carpet.mixins;

import carpet.CarpetSettings;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(Level.class)
public abstract class Level_fillUpdatesMixin
{
    @ModifyConstant(method = "markAndNotifyBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/chunk/LevelChunk;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/block/state/BlockState;II)V", //setBlockState main
            constant = @Constant(intValue = 16))
    private int addFillUpdatesInt(int original) {
        if (CarpetSettings.impendingFillSkipUpdates.get())
            return -1;
        return original;
    }

    @WrapOperation(method = "markAndNotifyBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/chunk/LevelChunk;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/block/state/BlockState;II)V", at = @At( //setBlockState main
            value = "INVOKE",
            target = "Lnet/minecraft/world/level/Level;blockUpdated(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/Block;)V"
    ))
    private void updateNeighborsMaybe(Level world, BlockPos blockPos, Block block, Operation<Void> original)
    {
        if (!CarpetSettings.impendingFillSkipUpdates.get()) original.call(world, blockPos, block);
    }

}
