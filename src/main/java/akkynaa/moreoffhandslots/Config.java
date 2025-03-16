package akkynaa.moreoffhandslots;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;

@Mod.EventBusSubscriber(modid = MoreOffhandSlots.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class Config {
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();


    private static final ForgeConfigSpec.BooleanValue CYCLE_EMPTY_SLOTS = BUILDER
            .comment("Whether to cycle through empty slots")
            .define("cycleEmptySlots", false);

    private static final ForgeConfigSpec.BooleanValue RENDER_EMPTY_OFFHAND = BUILDER
            .comment("Weather to render the offhand slots when empty items are in them. (will only take effect if cycleEmptySlots is true)")
            .define("renderEmptyOffhand", false);

    static final ForgeConfigSpec SPEC = BUILDER.build();

    public static boolean cycleEmptySlots;

    public static boolean renderEmptyOffhand;

    @SubscribeEvent
    static void onLoad(final ModConfigEvent.Loading event) {
        cycleEmptySlots = CYCLE_EMPTY_SLOTS.get();
        renderEmptyOffhand = RENDER_EMPTY_OFFHAND.get();
    }

    @SubscribeEvent
    static void onReload(final ModConfigEvent.Reloading event) {
        cycleEmptySlots = CYCLE_EMPTY_SLOTS.get();
        renderEmptyOffhand = RENDER_EMPTY_OFFHAND.get();
    }
}