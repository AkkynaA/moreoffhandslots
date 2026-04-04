package net.akkynaa.moreoffhandslots.network.payload;

import net.akkynaa.moreoffhandslots.capability.OffhandRegistry;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import javax.annotation.Nonnull;

public record PlayerOffhandPositionSyncPayload(int position) implements CustomPacketPayload {

    public static final Type<PlayerOffhandPositionSyncPayload> TYPE =
            new Type<>(Identifier.fromNamespaceAndPath("moreoffhandslots", "offhand_position_sync"));

    public static final StreamCodec<RegistryFriendlyByteBuf, PlayerOffhandPositionSyncPayload> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.INT, PlayerOffhandPositionSyncPayload::position,
                    PlayerOffhandPositionSyncPayload::new
            );

    public static void handleClient(PlayerOffhandPositionSyncPayload payload, final IPayloadContext context) {
        context.enqueueWork(() -> {
            Player player = context.player();
            player.getData(OffhandRegistry.OFFHAND_POSITION).setPosition(payload.position());
        });
    }

    @Nonnull
    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}