package akkynaa.moreoffhandslots;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.apache.commons.lang3.tuple.Pair;
import java.util.List;


public class Config {


    public static final Client CLIENT;
    public static final ModConfigSpec CONFIG_SPEC;


    static {
        Pair<Client, ModConfigSpec> pair =
                new ModConfigSpec.Builder().configure(Client::new);

        CLIENT = pair.getLeft();
        CONFIG_SPEC = pair.getRight();
    }


    public static class Client {
        public final ModConfigSpec.BooleanValue CYCLE_EMPTY_SLOTS;
        public final ModConfigSpec.BooleanValue RENDER_EMPTY_OFFHAND;
        public final ModConfigSpec.BooleanValue USE_SCROLL_FOR_OFFHAND;
        public final ModConfigSpec.BooleanValue INVERT_SCROLL_DIRECTION;
        public final ModConfigSpec.ConfigValue<String> SCROLL_SHIFT_MODE;

        private Client(ModConfigSpec.Builder builder) {
            builder.push("client");

            CYCLE_EMPTY_SLOTS = builder
                    .comment("Whether to cycle through empty slots.")
                    .translation("config.moreoffhandslots.cycleEmptySlots")
                    .define("cycleEmptySlots", false);

            RENDER_EMPTY_OFFHAND = builder
                    .comment("Weather to render the offhand slots when empty items are in them. (will only take effect if cycleEmptySlots is true)")
                    .translation("config.moreoffhandslots.renderEmptyOffhand")
                    .define("renderEmptyOffhand", false);

            USE_SCROLL_FOR_OFFHAND = builder
                    .comment("Use mouse scroll wheel to cycle through offhand items instead of hotbar slots.")
                    .translation("config.moreoffhandslots.useScrollForOffhand")
                    .define("useScrollForOffhand", false);

            INVERT_SCROLL_DIRECTION = builder
                    .comment("Invert the direction of the scroll wheel for cycling through offhand items. (will only take effect if useScrollForOffhand is true)")
                    .translation("config.moreoffhandslots.invertScrollDirection")
                    .define("invertScrollDirection", false);

            SCROLL_SHIFT_MODE = builder
                    .comment(
                            """
                                    The mode to use when shift is held while scrolling. (will only take effect if useScrollForOffhand is true)\
                                    
                                    none: Do nothing, keep the scrollwheel functionality for offhand only.\
                                    
                                    mainhand: When shift is held, scroll through the mainhand slots, otherwise scroll through the offhand slots.\
                                    
                                    offhand: When shift is held, scroll through the offhand slots, otherwise scroll through the mainhand slots."""
                    )
                    .translation("config.moreoffhandslots.scrollShiftMode")
                    .define("scrollShiftMode", "none");

            builder.pop();
        }

    }


    @SubscribeEvent
    public static void onLoad(ModConfigEvent.Loading event) {

    }

    @SubscribeEvent
    public static void onReload(ModConfigEvent.Reloading event) {

    }
}