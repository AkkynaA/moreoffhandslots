package akkynaa.moreoffhandslots;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = MoreOffhandSlots.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ClientEvents {

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        // Register our custom renderer
        event.enqueueWork(() -> {
            // Nothing to do here since we're using event subscribers
            // This runs on the mod loading thread
        });
    }

    @SubscribeEvent
    public static void onRegisterKeyMappings(RegisterKeyMappingsEvent event) {
        // Register keybindings
        event.register(KeyBindings.NEXT_OFFHAND_KEY);
        event.register(KeyBindings.PREV_OFFHAND_KEY);
    }
}