package akkynaa.moreoffhandslots.client.input;

import akkynaa.moreoffhandslots.network.payload.CycleOffhandPayload;
import akkynaa.moreoffhandslots.MoreOffhandSlots;
import akkynaa.moreoffhandslots.client.config.ClientConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.InputEvent.MouseScrollingEvent;
import net.neoforged.neoforge.network.PacketDistributor;


/**
 * Handles redirecting mouse wheel scrolling to cycle through offhand items
 * when the useScrollForOffhand config option is enabled.
 */
@EventBusSubscriber(modid = MoreOffhandSlots.MODID, bus = EventBusSubscriber.Bus.GAME, value = Dist.CLIENT)
public class ScrollWheelHandler {

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onMouseScroll(MouseScrollingEvent event) {

        if (!ClientConfig.CLIENT.USE_SCROLL_FOR_OFFHAND.get()) {
            return;
        }

        Minecraft minecraft = Minecraft.getInstance();
        LocalPlayer player = minecraft.player;

        if (player == null || minecraft.screen != null) {
            return;
        }

        // Get the scroll direction (positive = up/away, negative = down/toward)
        double scrollDelta = event.getScrollDeltaX();
        // Direction is: true for next (scroll down), false for previous (scroll up)
        boolean direction = scrollDelta < 0;

        switch (ClientConfig.CLIENT.SCROLL_SHIFT_MODE.get()) {
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
        if (ClientConfig.CLIENT.INVERT_SCROLL_DIRECTION.get()) {
            direction = !direction;
        }
        PacketDistributor.sendToServer(new CycleOffhandPayload(direction, ClientConfig.CLIENT.CYCLE_EMPTY_SLOTS.get()));
    }
}