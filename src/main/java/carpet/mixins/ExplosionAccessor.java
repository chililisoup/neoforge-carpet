package carpet.mixins;

import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;

@Mixin(Explosion.class)
public interface ExplosionAccessor {

    @Accessor("fire")
    boolean carpet$isFire();

    @Accessor("blockInteraction")
    Explosion.BlockInteraction carpet$getBlockInteraction();

    @Accessor("level")
    Level carpet$getLevel();

    @Accessor("random")
    RandomSource carpet$getRandom();

    @Accessor("x")
    double carpet$getX();

    @Accessor("y")
    double carpet$getY();

    @Accessor("z")
    double carpet$getZ();

    @Accessor("radius")
    float carpet$getRadius();

    @Accessor("source")
    Entity carpet$getSource();

    @Accessor("damageSource")
    DamageSource carpet$getDamageSource();

}
