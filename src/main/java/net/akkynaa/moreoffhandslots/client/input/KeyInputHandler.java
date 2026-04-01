package net.akkynaa.moreoffhandslots.client.input;

import net.akkynaa.moreoffhandslots.MoreOffhandSlots;
import net.akkynaa.moreoffhandslots.client.config.ClientConfig;
import net.akkynaa.moreoffhandslots.network.payload.CycleOffhandPayload;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;

@EventBusSubscriber(modid = MoreOffhandSlots.MODID, value = Dist.CLIENT)
public class KeyInputHandler {

    @SubscribeEvent
    public static void onKeyInput(ClientTickEvent.Post event) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) return;

        int emptySlotBehavior = ClientConfig.EMPTY_SLOT_BEHAVIOR.get().ordinal();

        if (KeyBindings.NEXT_OFFHAND_KEY.consumeClick()) {
            PacketDistributor.sendToServer(new CycleOffhandPayload(true, emptySlotBehavior));
        }

        if (KeyBindings.PREV_OFFHAND_KEY.consumeClick()) {
            PacketDistributor.sendToServer(new CycleOffhandPayload(false, emptySlotBehavior));
        }
    }
}