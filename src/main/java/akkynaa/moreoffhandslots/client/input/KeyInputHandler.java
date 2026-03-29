package akkynaa.moreoffhandslots.client.input;

import akkynaa.moreoffhandslots.client.config.ClientConfig;
import akkynaa.moreoffhandslots.MoreOffhandSlots;
import akkynaa.moreoffhandslots.network.PacketHandler;
import akkynaa.moreoffhandslots.network.message.CycleOffhandMessage;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = MoreOffhandSlots.MODID, value = Dist.CLIENT)
public class KeyInputHandler {

    @SubscribeEvent
    public static void onKeyInput(InputEvent.Key event) {
        int emptySlotBehavior = ClientConfig.EMPTY_SLOT_BEHAVIOR.get().ordinal();

        if (KeyBindings.NEXT_OFFHAND_KEY.consumeClick()) {
            PacketHandler.INSTANCE.sendToServer(new CycleOffhandMessage(true, emptySlotBehavior));
        }

        if (KeyBindings.PREV_OFFHAND_KEY.consumeClick()) {
            PacketHandler.INSTANCE.sendToServer(new CycleOffhandMessage(false, emptySlotBehavior));
        }
    }

    @SubscribeEvent
    public static void onMouseInput(InputEvent.MouseButton event) {
        int emptySlotBehavior = ClientConfig.EMPTY_SLOT_BEHAVIOR.get().ordinal();

        if (KeyBindings.NEXT_OFFHAND_KEY.consumeClick()) {
            PacketHandler.INSTANCE.sendToServer(new CycleOffhandMessage(true, emptySlotBehavior));
        }

        if (KeyBindings.PREV_OFFHAND_KEY.consumeClick()) {
            PacketHandler.INSTANCE.sendToServer(new CycleOffhandMessage(false, emptySlotBehavior));
        }
    }
}
