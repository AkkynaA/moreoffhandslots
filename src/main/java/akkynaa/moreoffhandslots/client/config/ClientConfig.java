package akkynaa.moreoffhandslots.client.config;

import akkynaa.moreoffhandslots.MoreOffhandSlots;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;

import java.util.List;

@Mod.EventBusSubscriber(modid = MoreOffhandSlots.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ClientConfig {

    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();


    private static final ForgeConfigSpec.BooleanValue CYCLE_EMPTY_SLOTS = BUILDER
            .comment("Whether to cycle through empty slots.")
            .define("cycleEmptySlots", false);

    private static final ForgeConfigSpec.BooleanValue RENDER_EMPTY_OFFHAND = BUILDER
            .comment("Weather to render the offhand slots when empty items are in them. (will only take effect if cycleEmptySlots is true)")
            .define("renderEmptyOffhand", false);

    private static final ForgeConfigSpec.BooleanValue USE_SCROLL_FOR_OFFHAND = BUILDER
            .comment("Use mouse scroll wheel to cycle through offhand items instead of hotbar slots.")
            .define("useScrollForOffhand", false);

    private static final ForgeConfigSpec.BooleanValue INVERT_SCROLL_DIRECTION = BUILDER
            .comment("Invert the direction of the scroll wheel for cycling through offhand items. (will only take effect if useScrollForOffhand is true)")
            .define("invertScrollDirection", false);

    private static final ForgeConfigSpec.ConfigValue<String> SCROLL_SHIFT_MODE = BUILDER
            .comment(
                    """
                    The mode to use when shift is held while scrolling. (will only take effect if useScrollForOffhand is true)\
                    
                    none: Do nothing, keep the scrollwheel functionality for offhand only.\
                    
                    mainhand: When shift is held, scroll through the mainhand slots, otherwise scroll through the offhand slots.\
                    
                    offhand: When shift is held, scroll through the offhand slots, otherwise scroll through the mainhand slots."""
            )
            .define("scrollShiftMode", "none");

    public static final ForgeConfigSpec SPEC = BUILDER.build();

    public static boolean cycleEmptySlots;
    public static boolean renderEmptyOffhand;
    public static boolean useScrollForOffhand;
    public static boolean invertScrollDirection;
    public static String scrollShiftMode;

    @SubscribeEvent
    static void onLoad(final ModConfigEvent.Loading event) {
        cycleEmptySlots = CYCLE_EMPTY_SLOTS.get();
        renderEmptyOffhand = RENDER_EMPTY_OFFHAND.get();
        useScrollForOffhand = USE_SCROLL_FOR_OFFHAND.get();
        invertScrollDirection = INVERT_SCROLL_DIRECTION.get();
        scrollShiftMode = SCROLL_SHIFT_MODE.get();
    }

    @SubscribeEvent
    static void onReload(final ModConfigEvent.Reloading event) {
        cycleEmptySlots = CYCLE_EMPTY_SLOTS.get();
        renderEmptyOffhand = RENDER_EMPTY_OFFHAND.get();
        useScrollForOffhand = USE_SCROLL_FOR_OFFHAND.get();
        invertScrollDirection = INVERT_SCROLL_DIRECTION.get();
        scrollShiftMode = SCROLL_SHIFT_MODE.get();
    }
}