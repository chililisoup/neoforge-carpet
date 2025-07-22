package carpet.mixins;

import carpet.fakes.BlockBehaviourInterface;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.extensions.IForgeBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(IForgeBlock.class)
public interface IForgeBlock_customStickyMixin {
    /**
     * @author chililisoup
     * @reason default to carpet's sticky check. Can still be overridden
     */
    @Overwrite(remap = false)
    default boolean isStickyBlock(BlockState state) {
        Block block = state.getBlock();
        if (block instanceof BlockBehaviourInterface behaviourInterface)
            return behaviourInterface.isSticky(state);
        else return false;
    }
}
