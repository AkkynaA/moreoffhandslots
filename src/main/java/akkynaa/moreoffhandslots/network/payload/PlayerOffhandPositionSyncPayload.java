package akkynaa.moreoffhandslots.network.payload;

import akkynaa.moreoffhandslots.capability.OffhandRegistry;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.event.level.NoteBlockEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import javax.annotation.Nonnull;

public record PlayerOffhandPositionSyncPayload(int position) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<PlayerOffhandPositionSyncPayload> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath("moreoffhandslots", "offhand_position_sync"));

    public static final StreamCodec<RegistryFriendlyByteBuf, PlayerOffhandPositionSyncPayload> STREAM_CODEC = new StreamCodec<>() {

        @Nonnull
        @Override
        public PlayerOffhandPositionSyncPayload decode(RegistryFriendlyByteBuf buffer) {
            return new PlayerOffhandPositionSyncPayload(buffer.readInt());
        }

        @Override
        public void encode(RegistryFriendlyByteBuf buffer, PlayerOffhandPositionSyncPayload value) {
            buffer.writeInt(value.position());
        }
    };

    public static void handleServer(PlayerOffhandPositionSyncPayload payload,final IPayloadContext context) {
    }

    public static void handleClient(PlayerOffhandPositionSyncPayload payload, final IPayloadContext context) {
        context.enqueueWork(() -> {
            Player player = context.player();
            var data = player.getData(OffhandRegistry.OFFHAND_POSITION);
            data.setPosition(payload.position());
        });
    }

    @Nonnull
    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}