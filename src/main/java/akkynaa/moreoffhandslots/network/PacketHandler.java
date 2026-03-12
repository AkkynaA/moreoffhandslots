package akkynaa.moreoffhandslots.network;

import akkynaa.moreoffhandslots.network.payload.CycleOffhandPayload;
import akkynaa.moreoffhandslots.network.payload.PlayerOffhandPositionSyncPayload;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public class PacketHandler {
    private static final String VERSION = "1.0";

    @SubscribeEvent
    public static void register(final RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar(VERSION);
        // Client sends cycling request to server
        registrar.playToServer(
                CycleOffhandPayload.TYPE,
                CycleOffhandPayload.STREAM_CODEC,
                CycleOffhandPayload::handleServer
        );
        // Server sends position sync to client
        registrar.playToClient(
                PlayerOffhandPositionSyncPayload.TYPE,
                PlayerOffhandPositionSyncPayload.STREAM_CODEC,
                PlayerOffhandPositionSyncPayload::handleClient
        );
    }
}