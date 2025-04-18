package akkynaa.moreoffhandslots.mixin;

import akkynaa.moreoffhandslots.compat.AutoHudCompat;
import mod.crend.autohud.forge.AutoHudModEvents;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(AutoHudModEvents.class)
public class AutoHudCompatMixin {


    @Inject(
            method = "onClientSetup",
            at = @At("TAIL"),
            remap = false,
            require = 0
    )
    private static void onClientSetup(FMLClientSetupEvent event, CallbackInfo ci) {
        if (ModList.get().isLoaded("autohud")) {
            AutoHudCompat.register();
        }
    }

}
