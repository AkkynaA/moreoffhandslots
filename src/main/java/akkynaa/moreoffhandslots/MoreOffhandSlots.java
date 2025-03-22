package akkynaa.moreoffhandslots;

import akkynaa.moreoffhandslots.client.config.ClientConfig;
import akkynaa.moreoffhandslots.client.input.KeyBindings;
import akkynaa.moreoffhandslots.client.render.OffhandHudRenderer;
import akkynaa.moreoffhandslots.network.PacketHandler;
import com.mojang.logging.LogUtils;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import org.slf4j.Logger;


@Mod(MoreOffhandSlots.MODID)
public class MoreOffhandSlots {
    public static final String MODID = "moreoffhandslots";
    private static final Logger LOGGER = LogUtils.getLogger();

    public MoreOffhandSlots(IEventBus modEventBus, ModContainer modContainer) {
        LOGGER.info("Initializing More Offhand Slots mod");

        modEventBus.addListener(this::registerPacketHandler);
        modEventBus.addListener(this::clientSetup);

        //NeoForge.EVENT_BUS.register(this); :'((

        modContainer.registerConfig(ModConfig.Type.CLIENT, ClientConfig.CONFIG_SPEC);
        modContainer.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
    }


    private void registerPacketHandler(final RegisterPayloadHandlersEvent event) {
        LOGGER.info("Registering packet handler");

        PacketHandler.register(event);

        LOGGER.info("Packet handler registered");

    }

    private void clientSetup(final FMLClientSetupEvent event) {
        LOGGER.info("MoreOffhandSlots client setup starting");

        event.enqueueWork(() -> NeoForge.EVENT_BUS.register(OffhandHudRenderer.class));


        LOGGER.info("MoreOffhandSlots client setup complete");
    }

    @EventBusSubscriber(modid = MoreOffhandSlots.MODID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
    public static class Client {

        @SubscribeEvent
        public static void registerKeyBindings(RegisterKeyMappingsEvent event) {
            event.register(KeyBindings.NEXT_OFFHAND_KEY);
            event.register(KeyBindings.PREV_OFFHAND_KEY);
        }

    }

}