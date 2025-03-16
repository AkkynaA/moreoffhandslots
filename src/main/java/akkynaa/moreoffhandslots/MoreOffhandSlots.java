package akkynaa.moreoffhandslots;

import com.mojang.logging.LogUtils;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
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

        context.registerConfig(ModConfig.Type.CLIENT, Config.SPEC);
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
}