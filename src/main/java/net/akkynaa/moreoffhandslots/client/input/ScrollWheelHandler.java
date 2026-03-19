package net.akkynaa.moreoffhandslots.client.input;

import net.akkynaa.moreoffhandslots.api.OffhandInventory;
import net.akkynaa.moreoffhandslots.network.payload.CycleOffhandPayload;
import net.akkynaa.moreoffhandslots.MoreOffhandSlots;
import net.akkynaa.moreoffhandslots.client.config.ClientConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.Entity;
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

        if (ClientConfig.SCROLL_MODE.get().equals(ClientConfig.ScrollMode.VANILLA)) {
            return;
        }

        Minecraft minecraft = Minecraft.getInstance();
        LocalPlayer player = minecraft.player;

        if (player == null || minecraft.screen != null) {
            return;
        }
        // Get the scroll direction (positive = up/away, negative = down/toward)
        double scrollDelta = event.getScrollDeltaY();
        // Direction is: true for next (scroll down), false for previous (scroll up)
        boolean direction = scrollDelta < 0;

        switch (ClientConfig.SCROLL_MODE.get()) {
            case ClientConfig.ScrollMode.MAINHAND_WITH_MODIFIER -> {
                if (KeyBindings.SCROLLWHEEL_MODIFIER.isDown()) {
                    return;
                }
                performScrollAction(direction);
            }
            case ClientConfig.ScrollMode.OFFHAND_WITH_MODIFIER -> {
                if (KeyBindings.SCROLLWHEEL_MODIFIER.isDown()) {
                    performScrollAction(direction);
                } else {
                    return;
                }
            }
            case ClientConfig.ScrollMode.OFFHAND_ONLY -> performScrollAction(direction);
            default -> {
                return;
            }
        }

        // Cancel the event to prevent vanilla hotbar scrolling
        event.setCanceled(true);
    }

    private static void performScrollAction(boolean direction) {
        if (ClientConfig.INVERT_SCROLL_DIRECTION.get()) {
            direction = !direction;
        }
        PacketDistributor.sendToServer(new CycleOffhandPayload(direction, ClientConfig.CYCLE_EMPTY_SLOTS.get()));
    }
}