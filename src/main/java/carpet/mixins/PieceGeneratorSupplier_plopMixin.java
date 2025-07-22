package carpet.mixins;

import carpet.CarpetSettings;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.structure.pieces.PieceGenerator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import java.util.Optional;
import java.util.function.Predicate;
import net.minecraft.world.level.levelgen.structure.pieces.PieceGeneratorSupplier;

@Mixin(PieceGeneratorSupplier.class)
public interface PieceGeneratorSupplier_plopMixin
{
    /**
     * @author chililisoup
     * @reason Same as vanilla method unless generation check skipping is enabled
     */
    @Overwrite
    static <C extends FeatureConfiguration> PieceGeneratorSupplier<C> simple(Predicate<PieceGeneratorSupplier.Context<C>> pieceGeneratorPredicate, PieceGenerator<C> pieceGenerator) {
        Optional<PieceGenerator<C>> optional = Optional.of(pieceGenerator);

        return context -> (CarpetSettings.skipGenerationChecks.get() || pieceGeneratorPredicate.test(context))
                ? optional : Optional.empty();
    }
}
