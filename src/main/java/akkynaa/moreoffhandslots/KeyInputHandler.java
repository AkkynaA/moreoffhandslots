package akkynaa.moreoffhandslots;


import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.network.PacketDistributor;

@EventBusSubscriber(modid = MoreOffhandSlots.MODID, bus = EventBusSubscriber.Bus.GAME, value = Dist.CLIENT)
public class KeyInputHandler {

    @SubscribeEvent
    public static void onKeyInput(InputEvent.Key event) {

        if (KeyBindings.NEXT_OFFHAND_KEY.consumeClick()) {
            PacketDistributor.sendToServer(new CycleOffhandPayload(true));
        }

        if (KeyBindings.PREV_OFFHAND_KEY.consumeClick()) {
            PacketDistributor.sendToServer(new CycleOffhandPayload(false));
        }
    }


}