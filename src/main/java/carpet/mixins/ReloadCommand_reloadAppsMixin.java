package carpet.mixins;

import carpet.CarpetServer;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.commands.ReloadCommand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ReloadCommand.class)
public class ReloadCommand_reloadAppsMixin {
    @WrapOperation(
            method = "register", at = @At(
            value = "INVOKE",
            target = "Lcom/mojang/brigadier/builder/LiteralArgumentBuilder;executes(Lcom/mojang/brigadier/Command;)Lcom/mojang/brigadier/builder/ArgumentBuilder;"
    ))
    private static <T extends CommandSourceStack> ArgumentBuilder<T, LiteralArgumentBuilder<T>> onReload(
            LiteralArgumentBuilder<T> instance, Command<T> command, Operation<ArgumentBuilder<T, LiteralArgumentBuilder<T>>> original
    ) {
        return instance.executes(context -> {
            command.run(context);

            // can't fetch here the reference to the server
            CarpetServer.onReload(context.getSource().getServer());

            return 0;
        });
    }
}
