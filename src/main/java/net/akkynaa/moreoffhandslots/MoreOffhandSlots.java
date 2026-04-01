package net.akkynaa.moreoffhandslots;

import com.mojang.logging.LogUtils;
import net.akkynaa.moreoffhandslots.capability.OffhandRegistry;
import net.akkynaa.moreoffhandslots.client.config.ClientConfig;
import net.akkynaa.moreoffhandslots.client.input.KeyBindings;
import net.akkynaa.moreoffhandslots.client.render.OffhandHudRenderer;
import net.akkynaa.moreoffhandslots.network.PacketHandler;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import org.slf4j.Logger;


@Mod(MoreOffhandSlots.MODID)
public class MoreOffhandSlots {
    public static final String MODID = "moreoffhandslots";
    private static final Logger LOGGER = LogUtils.getLogger();

    public MoreOffhandSlots(IEventBus modEventBus, ModContainer modContainer) {
        OffhandRegistry.init(modEventBus);
        LOGGER.info("Initializing More Offhand Slots mod");

        modEventBus.addListener(this::registerPacketHandler);
        if (FMLEnvironment.dist.isClient())
            modEventBus.addListener(this::registerOffhandHudRenderer);
    }


    private void registerPacketHandler(final RegisterPayloadHandlersEvent event) {
        PacketHandler.register(event);
    }

    @SubscribeEvent
    private void registerOffhandHudRenderer(final RegisterGuiLayersEvent event) {
        event.registerAbove(
                VanillaGuiLayers.HOTBAR,
                ResourceLocation.fromNamespaceAndPath(MoreOffhandSlots.MODID, "offhand_hud"),
                (GuiGraphics guiGraphics, DeltaTracker deltaTracker) -> {
                    OffhandHudRenderer.getOffhandRenderer().renderOffhandHud(guiGraphics, deltaTracker);
                }
        );
    }

    @EventBusSubscriber(modid = MoreOffhandSlots.MODID, value = Dist.CLIENT)
    public static class KeyBindingRegistration {

        @SubscribeEvent
        public static void registerKeyBindings(RegisterKeyMappingsEvent event) {
            event.register(KeyBindings.NEXT_OFFHAND_KEY);
            event.register(KeyBindings.PREV_OFFHAND_KEY);
            event.register(KeyBindings.SCROLLWHEEL_MODIFIER);
        }

    }

    @Mod(value = MoreOffhandSlots.MODID, dist = Dist.CLIENT)
    public static class MoreOffhandSlotsClient {
        public MoreOffhandSlotsClient(ModContainer modContainer) {
            LOGGER.info("Initializing More Offhand Slots client");

            modContainer.registerConfig(ModConfig.Type.CLIENT, ClientConfig.SPEC);
            modContainer.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
        }
    }

    @EventBusSubscriber
    public static class PlayerTickHandler {
        @SubscribeEvent
        private static void onPlayerTick(PlayerTickEvent.Pre event) {
            if (event.getEntity() instanceof ServerPlayer serverPlayer) {
                serverPlayer.getData(OffhandRegistry.OFFHAND_POSITION).updateData(serverPlayer);
            }
        }
    }

}