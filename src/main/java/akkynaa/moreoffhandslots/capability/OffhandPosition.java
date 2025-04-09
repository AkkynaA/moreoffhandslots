package akkynaa.moreoffhandslots.capability;

import akkynaa.moreoffhandslots.api.OffhandInventory;
import akkynaa.moreoffhandslots.network.payload.PlayerOffhandPositionSyncPayload;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.common.util.INBTSerializable;
import net.neoforged.neoforge.network.PacketDistributor;
import javax.annotation.Nonnull;

public class OffhandPosition implements INBTSerializable<CompoundTag> {
    private int position = 0;

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public void changePosition(Player player, int change) {
        int offhandItemsLength = OffhandInventory.getOffhandItemsLength(player);
        position = (position + change + offhandItemsLength) % offhandItemsLength;
    }

    public void updateData(Player player) {
        PacketDistributor.sendToPlayer((ServerPlayer) player,
                new PlayerOffhandPositionSyncPayload(position));
    }

    @Override
    public CompoundTag serializeNBT(@Nonnull HolderLookup.Provider provider) {
        CompoundTag tag = new CompoundTag();
        tag.putInt("position", position);
        return tag;
    }

    @Override
    public void deserializeNBT(@Nonnull HolderLookup.Provider provider, CompoundTag nbt) {
        this.position = nbt.getInt("position");
    }
}