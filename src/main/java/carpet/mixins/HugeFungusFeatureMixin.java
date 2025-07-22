package carpet.mixins;

import carpet.CarpetSettings;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.levelgen.feature.HugeFungusConfiguration;
import net.minecraft.world.level.levelgen.feature.HugeFungusFeature;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import static carpet.CarpetSettings.FungusGrowthMode.*;

@Mixin(HugeFungusFeature.class)
public class HugeFungusFeatureMixin {
    @WrapOperation(
            method = "place", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/level/levelgen/feature/HugeFungusFeature;placeStem(Lnet/minecraft/world/level/WorldGenLevel;Lnet/minecraft/util/RandomSource;Lnet/minecraft/world/level/levelgen/feature/HugeFungusConfiguration;Lnet/minecraft/core/BlockPos;IZ)V"
    ))
    private void mixin(
            HugeFungusFeature fungusFeature,
            WorldGenLevel level,
            RandomSource random,
            HugeFungusConfiguration fungusConfiguration,
            BlockPos blockPos,
            int height,
            boolean shouldPlace,
            Operation<Void> original
    ) {
        boolean natural = !fungusConfiguration.planted;

        shouldPlace = natural && shouldPlace ||
            !natural && (CarpetSettings.thickFungusGrowth == ALL ||
            CarpetSettings.thickFungusGrowth == RANDOM && random.nextFloat() < 0.06F);

        original.call(fungusFeature, level, random, fungusConfiguration, blockPos, height, shouldPlace);
    }
}
