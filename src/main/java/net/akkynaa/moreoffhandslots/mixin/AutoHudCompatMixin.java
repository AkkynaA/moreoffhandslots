//package net.akkynaa.moreoffhandslots.mixin;
//
//import mod.crend.autohud.neoforge.AutoHudModEvents;
//import net.akkynaa.moreoffhandslots.compat.AutoHudCompat;
//import net.neoforged.fml.ModList;
//import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
//import org.spongepowered.asm.mixin.Mixin;
//import org.spongepowered.asm.mixin.injection.At;
//import org.spongepowered.asm.mixin.injection.Inject;
//import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
//
//
//@Mixin(AutoHudModEvents.class)
//public class AutoHudCompatMixin {
//
//
//    @Inject(
//            method = "onClientSetup",
//            at = @At("TAIL"),
//            remap = false,
//            require = 0
//    )
//    private static void onClientSetup(FMLClientSetupEvent event, CallbackInfo ci) {
//        if (ModList.get().isLoaded("autohud")) {
//            AutoHudCompat.register();
//        }
//    }
//
//}
