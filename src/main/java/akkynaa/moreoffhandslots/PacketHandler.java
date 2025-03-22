package akkynaa.moreoffhandslots;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.Optional;

public class PacketHandler {
    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
            ResourceLocation.fromNamespaceAndPath(MoreOffhandSlots.MODID, "main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    private static int id = 0;


    public static void register() {
        // Register packets
        INSTANCE.registerMessage(id++,
                CycleOffhandMessage.class,
                CycleOffhandMessage::encode,
                CycleOffhandMessage::decode,
                CycleOffhandMessage::handle,
                Optional.of(NetworkDirection.PLAY_TO_SERVER));
    }
}