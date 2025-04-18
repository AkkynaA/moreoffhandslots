package akkynaa.moreoffhandslots;

import akkynaa.moreoffhandslots.client.config.ClientConfig;
import akkynaa.moreoffhandslots.client.input.KeyBindings;
import akkynaa.moreoffhandslots.client.render.OffhandHudRenderer;
import akkynaa.moreoffhandslots.network.PacketHandler;
import com.mojang.logging.LogUtils;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
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

        if (Dist.CLIENT.isClient()) {
            modEventBus.addListener(this::registerOffhandHudRenderer);
        }


        context.registerConfig(ModConfig.Type.CLIENT, ClientConfig.SPEC);

    }

    private void setup(final FMLCommonSetupEvent event) {
        LOGGER.info("MoreOffhandSlots setup starting");

        PacketHandler.register();

        LOGGER.info("MoreOffhandSlots setup complete");
    }

    private void clientSetup(final FMLClientSetupEvent event) {
        LOGGER.info("MoreOffhandSlots client setup starting");

        LOGGER.info("MoreOffhandSlots client setup complete");
    }

    @SubscribeEvent
    public void registerOffhandHudRenderer(RegisterGuiOverlaysEvent event) {
        LOGGER.info("Registering Offhand HUD renderer");
        event.registerAbove(
                VanillaGuiOverlay.HOTBAR.id(),
                ResourceLocation.fromNamespaceAndPath(MoreOffhandSlots.MODID, "offhand_hud").getPath(),
                (ForgeGui forgeGui, GuiGraphics guiGraphics, float  partialTicks, int screenWidth, int screenHeight) -> {
                    OffhandHudRenderer.getOffhandRenderer().renderOffhandHud(guiGraphics,  partialTicks, screenWidth, screenHeight);
                }
        );



        LOGGER.info("Offhand HUD renderer registered");
    }

    @Mod.EventBusSubscriber(modid = MoreOffhandSlots.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class MoreOffhandSlotsClient {

        @SubscribeEvent
        public static void registerKeyBindings(RegisterKeyMappingsEvent event) {
            event.register(KeyBindings.NEXT_OFFHAND_KEY);
            event.register(KeyBindings.PREV_OFFHAND_KEY);
        }

    }

}