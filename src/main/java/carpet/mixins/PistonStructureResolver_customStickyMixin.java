package carpet.mixins;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import carpet.fakes.BlockBehaviourInterface;
import com.llamalad7.mixinextras.sugar.Local;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.piston.PistonStructureResolver;
import net.minecraft.world.level.block.state.BlockState;

@Mixin(PistonStructureResolver.class)
public class PistonStructureResolver_customStickyMixin {

    @Shadow @Final private Level level;
    @Shadow @Final private Direction pushDirection;

    @WrapOperation(
        method = "addBlockLine",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/level/block/state/BlockState;canStickTo(Lnet/minecraft/world/level/block/state/BlockState;)Z",
            ordinal = 0
        )
    )
    private boolean onAddBlockLineCanStickToEachOther(
            BlockState state,
            BlockState behindState,
            Operation<Boolean> original,
            @Local(ordinal = 1) BlockPos behindPos
    ) {
        if (state.getBlock() instanceof BlockPistonBehaviourInterface behaviourInterface) {
            return behaviourInterface.isStickyToNeighbor(
                    level,
                    behindPos.relative(pushDirection),
                    state,
                    behindPos,
                    behindState,
                    pushDirection.getOpposite(),
                    pushDirection
            );
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

    @WrapOperation(
        method = "addBranchingBlocks",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/level/block/state/BlockState;canStickTo(Lnet/minecraft/world/level/block/state/BlockState;)Z",
            ordinal = 0
        )
    )
    private boolean onAddBranchingBlocksCanStickToEachOther(
            BlockState neighborState,
            BlockState state,
            Operation<Boolean> original,
            @Local(argsOnly = true) BlockPos pos,
            @Local(ordinal = 1) BlockPos neighborPos,
            @Local Direction direction
    ) {
        if (state.getBlock() instanceof BlockPistonBehaviourInterface behaviourInterface) {
            return behaviourInterface.isStickyToNeighbor(level, pos, state, neighborPos, neighborState, direction, pushDirection);
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
