package carpet.mixins;

import carpet.CarpetSettings;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.structure.pieces.PieceGenerator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;

import java.util.Optional;
import java.util.function.Predicate;

import net.minecraft.world.level.levelgen.structure.pieces.PieceGeneratorSupplier;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PieceGeneratorSupplier.class)
public interface PieceGeneratorSupplier_plopMixin {

    // We could just overwrite the method, but i prefer to inject rather than overwrite if possible
    @Inject(method = "simple", at = @At(value = "HEAD"), cancellable = true)
    private static <C extends FeatureConfiguration> void maybeSkipPieceGenerationChecks(Predicate<PieceGeneratorSupplier.Context<C>> predicate, PieceGenerator<C> piece, CallbackInfoReturnable<PieceGeneratorSupplier<C>> cir) {
        // Skip the predicate test if the settings say to
        if (CarpetSettings.skipGenerationChecks.get()) {
            Optional<PieceGenerator<C>> optional = Optional.of(piece);
            cir.setReturnValue((context) -> optional);
            cir.cancel();
        }
    }
}
