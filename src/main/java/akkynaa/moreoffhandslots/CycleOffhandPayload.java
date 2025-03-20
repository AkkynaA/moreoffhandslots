package akkynaa.moreoffhandslots;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nonnull;

public record CycleOffhandPayload(boolean next) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<CycleOffhandPayload> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath("moreoffhandslots", "cycle_offhand"));

    public static final StreamCodec<RegistryFriendlyByteBuf, CycleOffhandPayload> STREAM_CODEC = new StreamCodec<>() {

        @Nonnull
        @Override
        public CycleOffhandPayload decode(RegistryFriendlyByteBuf buffer) {
            return new CycleOffhandPayload(buffer.readBoolean());
        }

        @Override
        public void encode(RegistryFriendlyByteBuf buffer, CycleOffhandPayload value) {
            buffer.writeBoolean(value.next());
        }
    };

    @Nonnull
    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}