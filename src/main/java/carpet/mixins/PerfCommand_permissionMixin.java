package carpet.mixins;

import carpet.CarpetSettings;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.commands.PerfCommand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.function.Predicate;

@Mixin(PerfCommand.class)
public class PerfCommand_permissionMixin
{
    @WrapOperation(
            method = "register", at = @At(
            value = "INVOKE",
            target = "Lcom/mojang/brigadier/builder/LiteralArgumentBuilder;requires(Ljava/util/function/Predicate;)Lcom/mojang/brigadier/builder/ArgumentBuilder;"
    ))
    private static <T extends CommandSourceStack> ArgumentBuilder<T, LiteralArgumentBuilder<T>> canRun(
            LiteralArgumentBuilder<T> instance, Predicate<T> predicate, Operation<ArgumentBuilder<T, LiteralArgumentBuilder<T>>> original
    ) {
        return instance.requires(source ->
                source.hasPermission(CarpetSettings.perfPermissionLevel)
        );
    }
}
