package akkynaa.moreoffhandslots.client.input;

import akkynaa.moreoffhandslots.network.payload.CycleOffhandPayload;
import akkynaa.moreoffhandslots.MoreOffhandSlots;
import akkynaa.moreoffhandslots.client.config.ClientConfig;
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

        boolean cycleEmptySlots = ClientConfig.CYCLE_EMPTY_SLOTS.get();

        if (KeyBindings.NEXT_OFFHAND_KEY.consumeClick()) {
            PacketDistributor.sendToServer(new CycleOffhandPayload(true, cycleEmptySlots));
        }

        if (KeyBindings.PREV_OFFHAND_KEY.consumeClick()) {
            PacketDistributor.sendToServer(new CycleOffhandPayload(false, cycleEmptySlots));
        }
    }
}