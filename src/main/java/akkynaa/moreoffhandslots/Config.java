package akkynaa.moreoffhandslots;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;
import java.util.List;

@EventBusSubscriber(modid = MoreOffhandSlots.MODID, bus = EventBusSubscriber.Bus.MOD)
public class Config {

    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();


    private static final ModConfigSpec.BooleanValue CYCLE_EMPTY_SLOTS = BUILDER
            .comment("Whether to cycle through empty slots.")
            .define("cycleEmptySlots", false);

    private static final ModConfigSpec.BooleanValue RENDER_EMPTY_OFFHAND = BUILDER
            .comment("Weather to render the offhand slots when empty items are in them. (will only take effect if cycleEmptySlots is true)")
            .define("renderEmptyOffhand", false);

    private static final ModConfigSpec.BooleanValue USE_SCROLL_FOR_OFFHAND = BUILDER
            .comment("Use mouse scroll wheel to cycle through offhand items instead of hotbar slots.")
            .define("useScrollForOffhand", false);

    private static final ModConfigSpec.BooleanValue INVERT_SCROLL_DIRECTION = BUILDER
            .comment("Invert the direction of the scroll wheel for cycling through offhand items. (will only take effect if useScrollForOffhand is true)")
            .define("invertScrollDirection", false);

    private static final ModConfigSpec.ConfigValue<String> SCROLL_SHIFT_MODE = BUILDER
            .comment(
                    """
                    The mode to use when shift is held while scrolling. (will only take effect if useScrollForOffhand is true)\
                    
                    none: Do nothing, keep the scrollwheel functionality for offhand only.\
                    
                    mainhand: When shift is held, scroll through the mainhand slots, otherwise scroll through the offhand slots.\
                    
                    offhand: When shift is held, scroll through the offhand slots, otherwise scroll through the mainhand slots."""
            )
            .defineInList("scrollShiftMode", "none", List.of("none", "mainhand", "offhand"));

    static final ModConfigSpec SPEC = BUILDER.build();

    public static boolean cycleEmptySlots;
    public static boolean renderEmptyOffhand;
    public static boolean useScrollForOffhand;
    public static boolean invertScrollDirection;
    public static String scrollShiftMode;

    @SubscribeEvent
    static void onLoad(final ModConfigEvent event) {
        cycleEmptySlots = CYCLE_EMPTY_SLOTS.get();
        renderEmptyOffhand = RENDER_EMPTY_OFFHAND.get();
        useScrollForOffhand = USE_SCROLL_FOR_OFFHAND.get();
        invertScrollDirection = INVERT_SCROLL_DIRECTION.get();
        scrollShiftMode = SCROLL_SHIFT_MODE.get();
    }
//
//    @SubscribeEvent
//    static void onReload(final ModConfigEvent.Reloading event) {
//        cycleEmptySlots = CYCLE_EMPTY_SLOTS.get();
//        renderEmptyOffhand = RENDER_EMPTY_OFFHAND.get();
//        useScrollForOffhand = USE_SCROLL_FOR_OFFHAND.get();
//        invertScrollDirection = INVERT_SCROLL_DIRECTION.get();
//        scrollShiftMode = SCROLL_SHIFT_MODE.get();
//    }
}