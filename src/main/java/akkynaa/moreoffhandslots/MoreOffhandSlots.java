package akkynaa.moreoffhandslots;

import akkynaa.moreoffhandslots.client.config.ClientConfig;
import akkynaa.moreoffhandslots.client.input.KeyBindings;
import akkynaa.moreoffhandslots.client.render.OffhandIndicatorRenderer;
import akkynaa.moreoffhandslots.network.PacketHandler;
import com.mojang.logging.LogUtils;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;


@Mod(MoreOffhandSlots.MODID)
public class MoreOffhandSlots {
    public static final String MODID = "moreoffhandslots";
    private static final Logger LOGGER = LogUtils.getLogger();

    public MoreOffhandSlots(FMLJavaModLoadingContext context) {
        LOGGER.info("Initializing More Offhand Slots mod");

        IEventBus modEventBus = context.getModEventBus();

        modEventBus.addListener(this::setup);
        modEventBus.addListener(this::clientSetup);

        MinecraftForge.EVENT_BUS.register(this);

        context.registerConfig(ModConfig.Type.CLIENT, ClientConfig.SPEC);
    }

    private void setup(final FMLCommonSetupEvent event) {
        LOGGER.info("MoreOffhandSlots setup starting");

        PacketHandler.register();

        LOGGER.info("MoreOffhandSlots setup complete");
    }

    private void clientSetup(final FMLClientSetupEvent event) {
        LOGGER.info("MoreOffhandSlots client setup starting");

        MinecraftForge.EVENT_BUS.register(OffhandIndicatorRenderer.class);

        LOGGER.info("MoreOffhandSlots client setup complete");
    }

    @Mod.EventBusSubscriber(modid = MoreOffhandSlots.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class Client {

        @SubscribeEvent
        public static void registerKeyBindings(RegisterKeyMappingsEvent event) {
            event.register(KeyBindings.NEXT_OFFHAND_KEY);
            event.register(KeyBindings.PREV_OFFHAND_KEY);
        }

    }
}