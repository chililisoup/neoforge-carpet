package carpet.mixins;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import carpet.fakes.BlockPistonBehaviourInterface;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.piston.PistonStructureResolver;
import net.minecraft.world.level.block.state.BlockState;

@Mixin(PistonStructureResolver.class)
public class PistonStructureResolver_customStickyMixin {

    @Shadow @Final private Level level;
    @Shadow @Final private Direction pushDirection;

    // fields that are needed because @Redirects cannot capture locals
    @Unique private BlockPos pos_addBlockLine;
    @Unique private BlockPos behindPos_addBlockLine;

    @Inject(
        method = "addBlockLine",
        at = @At(
                value = "INVOKE",
                ordinal = 1,
                target = "Lnet/minecraft/world/level/Level;getBlockState(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/block/state/BlockState;"
        )
    )
    private void captureBlockLinePositions(BlockPos pos, Direction fromDir, CallbackInfoReturnable<Boolean> cir, @Local(ordinal = 1) BlockPos behindPos) {
        pos_addBlockLine = behindPos.relative(pushDirection);
        behindPos_addBlockLine = behindPos;
    }

    @WrapOperation(
        method = "addBlockLine",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/level/block/state/BlockState;canStickTo(Lnet/minecraft/world/level/block/state/BlockState;)Z",
            ordinal = 0
        )
    )
    private boolean onAddBlockLineCanStickToEachOther(BlockState state, BlockState behindState, Operation<Boolean> original) {
        if (state.getBlock() instanceof BlockPistonBehaviourInterface behaviourInterface) {
            return behaviourInterface.isStickyToNeighbor(level, pos_addBlockLine, state, behindPos_addBlockLine, behindState, pushDirection.getOpposite(), pushDirection);
        }

        return original.call(state, behindState);
    }

    @WrapOperation(
            method = "addBlockLine",
            at = @At(
                value = "INVOKE",
                target = "Lnet/minecraft/world/level/block/state/BlockState;canStickTo(Lnet/minecraft/world/level/block/state/BlockState;)Z",
                ordinal = 1
            )
    )
    private boolean removeSecondBlockLineCheck(BlockState state, BlockState behindState, Operation<Boolean> original) {
        return true;
    }

    // fields that are needed because @Redirects cannot capture locals
    @Unique private Direction dir_addBranchingBlocks;
    @Unique private BlockPos neighborPos_addBranchingBlocks;

    @Inject(
        method = "addBranchingBlocks",
        at = @At(
                value = "INVOKE",
                ordinal = 1,
                target = "Lnet/minecraft/world/level/Level;getBlockState(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/block/state/BlockState;"
        )
    )
    private void captureNeighborPositions(BlockPos pos, CallbackInfoReturnable<Boolean> cir, @Local Direction dir, @Local(ordinal = 1) BlockPos neighborPos) {
        dir_addBranchingBlocks = dir;
        neighborPos_addBranchingBlocks = neighborPos;
    }

    @WrapOperation(
        method = "addBranchingBlocks",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/level/block/state/BlockState;canStickTo(Lnet/minecraft/world/level/block/state/BlockState;)Z",
            ordinal = 0
        )
    )
    private boolean onAddBranchingBlocksCanStickToEachOther(BlockState neighborState, BlockState state, Operation<Boolean> original, @Local(argsOnly = true) BlockPos pos) {
        if (state.getBlock() instanceof BlockPistonBehaviourInterface behaviourInterface) {
            return behaviourInterface.isStickyToNeighbor(level, pos, state, neighborPos_addBranchingBlocks, neighborState, dir_addBranchingBlocks, pushDirection);
        }

        return original.call(neighborState, state);
    }

    @WrapOperation(
            method = "addBranchingBlocks",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/block/state/BlockState;canStickTo(Lnet/minecraft/world/level/block/state/BlockState;)Z",
                    ordinal = 1
            )
    )
    private boolean removeSecondBranchingBlockCheck(BlockState neighborState, BlockState state, Operation<Boolean> original) {
        return true;
    }
}
