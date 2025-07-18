package akkynaa.moreoffhandslots;

import akkynaa.moreoffhandslots.client.config.ClientConfig;
import akkynaa.moreoffhandslots.client.input.KeyBindings;
import akkynaa.moreoffhandslots.client.render.OffhandHudRenderer;
import akkynaa.moreoffhandslots.network.PacketHandler;
import com.mojang.logging.LogUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.event.CurioAttributeModifierEvent;
import top.theillusivec4.curios.api.event.CurioEquipEvent;

import java.util.Objects;


@Mod(MoreOffhandSlots.MODID)
public class MoreOffhandSlots {
    public static final String MODID = "moreoffhandslots";
    private static final Logger LOGGER = LogUtils.getLogger();

    public MoreOffhandSlots(FMLJavaModLoadingContext context) {
        LOGGER.info("Initializing More Offhand Slots mod");

        IEventBus modEventBus = context.getModEventBus();

        MinecraftForge.EVENT_BUS.register(this);

        context.registerConfig(ModConfig.Type.CLIENT, ClientConfig.SPEC);

        modEventBus.addListener(this::setup);

        try {
            modEventBus.addListener(this::registerOffhandHudRenderer);
        } catch (NoSuchMethodError ignored) {} // F u forge



    }

    private void setup(final FMLCommonSetupEvent event) {
        LOGGER.info("MoreOffhandSlots setup starting");

        PacketHandler.register();

        LOGGER.info("MoreOffhandSlots setup complete");
    }

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public void registerOffhandHudRenderer(RegisterGuiOverlaysEvent event) {
        LOGGER.info("Registering Offhand HUD renderer");
        event.registerAbove(
                VanillaGuiOverlay.HOTBAR.id(),
                ResourceLocation.fromNamespaceAndPath(MoreOffhandSlots.MODID, "offhand_hud").getPath(),
                (forgeGui, guiGraphics, partialTicks,screenWidth, screenHeight) -> {
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
            event.register(KeyBindings.SCROLLWHEEL_MODIFIER);
        }

    }

    @Mod.EventBusSubscriber(modid = MoreOffhandSlots.MODID)
    public static class CurioHandler {
        @SubscribeEvent
        public static void onCuriosEquip(CurioAttributeModifierEvent event) {
            String slotId = event.getSlotContext().identifier();

            if (slotId.equals("offhand")) {
                event.clearModifiers();
            }
        }

    }

}