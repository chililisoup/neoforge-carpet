package carpet.mixins;

import carpet.CarpetServer;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.commands.ReloadCommand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Collection;

@Mixin(ReloadCommand.class)
public class ReloadCommand_reloadAppsMixin {
    @Inject(method = "reloadPacks", at = @At("HEAD"), remap = false)
    private static void onReload(Collection<String> p_138236_, CommandSourceStack context, CallbackInfo ci)
    {
        // can't fetch here the reference to the server
        CarpetServer.onReload(context.getServer());
    }
}
