package akkynaa.moreoffhandslots;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = MoreOffhandSlots.MODID, value = Dist.CLIENT)
public class KeyInputHandler {

    @SubscribeEvent
    public static void onKeyInput(InputEvent.Key event) {

        if (KeyBindings.NEXT_OFFHAND_KEY.consumeClick()) {
            PacketHandler.INSTANCE.sendToServer(new CycleOffhandMessage(true));
        }

        if (KeyBindings.PREV_OFFHAND_KEY.consumeClick()) {
            PacketHandler.INSTANCE.sendToServer(new CycleOffhandMessage(false));
        }
    }

    @SubscribeEvent
    public static void onMouseInput(InputEvent.MouseButton event) {

        if (KeyBindings.NEXT_OFFHAND_KEY.consumeClick()) {
            PacketHandler.INSTANCE.sendToServer(new CycleOffhandMessage(true));
        }

        if (KeyBindings.PREV_OFFHAND_KEY.consumeClick()) {
            PacketHandler.INSTANCE.sendToServer(new CycleOffhandMessage(false));
        }
    }
}