package akkynaa.moreoffhandslots.client.config;


import net.minecraftforge.common.ForgeConfigSpec;

public class ClientConfig {

    public enum ScrollMode {
        VANILLA,
        OFFHAND_ONLY,
        MAINHAND_WITH_MODIFIER,
        OFFHAND_WITH_MODIFIER
    }

    public enum IndicatorStyle {
        DEFAULT,
        DETAILED,
        VANILLA,
        HOTBAR,
    }


    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

    public static final ForgeConfigSpec.EnumValue<IndicatorStyle> INDICATOR_STYLE;
    public static final ForgeConfigSpec.BooleanValue CYCLE_EMPTY_SLOTS;
    public static final ForgeConfigSpec.BooleanValue RENDER_EMPTY_OFFHAND;
    public static final ForgeConfigSpec.EnumValue<ScrollMode> SCROLL_MODE;
    public static final ForgeConfigSpec.BooleanValue INVERT_SCROLL_DIRECTION;
    public static final ForgeConfigSpec.IntValue X_OFFSET;
    public static final ForgeConfigSpec.IntValue Y_OFFSET;
    public static final ForgeConfigSpec.BooleanValue ALIGN_TO_CENTER;


    static  {
        INDICATOR_STYLE = BUILDER
                .comment("""
#- DEFAULT: Default indicator style
#- DETAILED: Detailed indicator style, with item counts
#- VANILLA: Restores the vanilla indicator style, while keeping the mod's functionality
#- HOTBAR: Add a hotbar for the offhand slots, similar to the main hand hotbar
                """)
                .translation("config.moreoffhandslots.indicatorStyle")
                .defineEnum("indicatorStyle", IndicatorStyle.DEFAULT);

        CYCLE_EMPTY_SLOTS = BUILDER
                .comment("Whether to cycle through empty slots.")
                .translation("config.moreoffhandslots.cycleEmptySlots")
                .define("cycleEmptySlots", false);

        RENDER_EMPTY_OFFHAND = BUILDER
                .comment("Whether to render the offhand slots when empty items are in them. (will only take effect if cycleEmptySlots is true)")
                .translation("config.moreoffhandslots.renderEmptyOffhand")
                .define("renderEmptyOffhand", false);

        SCROLL_MODE = BUILDER
                .comment("""
#- VANILLA: Default Minecraft behavior (scroll cycles hotbar slots)
#- OFFHAND_ONLY: Scroll wheel always cycles through offhand slots
#- MAINHAND_WITH_MODIFIER: Normally cycles offhand, but cycles hotbar when modifier key is held.
#- OFFHAND_WITH_MODIFIER: Normally cycles hotbar, but cycles offhand when modifier key is held."""
                )
                .translation("config.moreoffhandslots.scrollMode")
                .defineEnum("scrollMode", ScrollMode.VANILLA);

        INVERT_SCROLL_DIRECTION = BUILDER
                .comment("Invert the direction of the scroll wheel for cycling through offhand items.")
                .translation("config.moreoffhandslots.invertScrollDirection")
                .define("invertScrollDirection", false);

        X_OFFSET = BUILDER
                .comment("X offset for the offhand HUD")
                .translation("config.moreoffhandslots.xOffset")
                .defineInRange("xOffset", 0, Integer.MIN_VALUE, Integer.MAX_VALUE);

        Y_OFFSET = BUILDER
                .comment("Y offset for the offhand HUD")
                .translation("config.moreoffhandslots.yOffset")
                .defineInRange("yOffset", 0, Integer.MIN_VALUE, Integer.MAX_VALUE);

        ALIGN_TO_CENTER
                = BUILDER
                .comment("Align the offhand hotbar and the vanilla hotbar to the center of the screen")
                .translation("config.moreoffhandslots.alignToCenter")
                .define("alignToCenter", false);

    }

    public static final ForgeConfigSpec SPEC = BUILDER.build();
}