package carpet.mixins;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import carpet.CarpetSettings;
import net.minecraft.commands.arguments.blocks.BlockInput;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;

@Mixin(BlockInput.class)
public class BlockInput_fillUpdatesMixin
{
    @WrapOperation(method = "place", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/level/block/Block;updateFromNeighbourShapes(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/LevelAccessor;Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/block/state/BlockState;"
    ))
    private BlockState postProcessStateProxy(BlockState state, LevelAccessor serverWorld, BlockPos blockPos, Operation<BlockState> original)
    {
        if (CarpetSettings.impendingFillSkipUpdates.get())
        {
            return state;
        }
        
        return original.call(state, serverWorld, blockPos);
    }
}
