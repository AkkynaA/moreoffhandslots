package akkynaa.moreoffhandslots;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * Handles redirecting mouse wheel scrolling to cycle through offhand items
 * when the useScrollForOffhand config option is enabled.
 */
@Mod.EventBusSubscriber(modid = MoreOffhandSlots.MODID)
public class ScrollWheelHandler {

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onMouseScroll(InputEvent.MouseScrollingEvent event) {

        if (!Config.useScrollForOffhand) {
            return;
        }

        Minecraft minecraft = Minecraft.getInstance();
        LocalPlayer player = minecraft.player;

        if (player == null || minecraft.screen != null) {
            return;
        }

        // Get the scroll direction (positive = up/away, negative = down/toward)
        double scrollDelta = event.getScrollDelta();
        // Direction is: true for next (scroll down), false for previous (scroll up)
        boolean direction = scrollDelta < 0;

        switch (Config.scrollShiftMode) {
            case "mainhand" -> {
                if (player.isShiftKeyDown()) {
                    return;
                }
                performScrollAction(direction);
            }
            case "offhand" -> {
                if (player.isShiftKeyDown()) {
                    performScrollAction(direction);
                } else {
                    return;
                }
            }
            default -> performScrollAction(direction);
        }

        // Cancel the event to prevent vanilla hotbar scrolling
        event.setCanceled(true);
    }

    private static void performScrollAction(boolean direction) {
        if (Config.invertScrollDirection) {
            direction = !direction;
        }
        PacketHandler.INSTANCE.sendToServer(new CycleOffhandMessage(direction));
    }
}