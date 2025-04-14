package akkynaa.moreoffhandslots.network.message;

import akkynaa.moreoffhandslots.capability.ModCapabilities;
import akkynaa.moreoffhandslots.capability.OffhandPositionManager;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.Objects;
import java.util.function.Supplier;

public class PlayerOffhandPositionSyncMessage {
    private final int position;

    public PlayerOffhandPositionSyncMessage(int position) {
        this.position = position;
    }

    public static void encode(PlayerOffhandPositionSyncMessage message, FriendlyByteBuf buffer) {
        buffer.writeInt(message.position);
    }

    public static PlayerOffhandPositionSyncMessage decode(FriendlyByteBuf buffer) {
        return new PlayerOffhandPositionSyncMessage(buffer.readInt());
    }

    public static void handle(PlayerOffhandPositionSyncMessage message, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        if (context.getDirection().getReceptionSide().isClient()) {
            context.enqueueWork(() ->
                    DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () ->
                            ClientOffhandPositionSyncMessage.handle(message, contextSupplier))
            );
        }
        context.setPacketHandled(true);
    }

    @OnlyIn(Dist.CLIENT)
    static
    class ClientOffhandPositionSyncMessage {
        public static void handle(PlayerOffhandPositionSyncMessage message, Supplier<NetworkEvent.Context> contextSupplier) {
            Player player = Minecraft.getInstance().player;
            if (player != null) {
                player.getCapability(ModCapabilities.OFFHAND_POSITION).ifPresent(offhandPosition -> {
                    offhandPosition.setPosition(message.position);
                });
            }

        }
    }
}
