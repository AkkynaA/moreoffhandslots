package akkynaa.moreoffhandslots.capability;

import akkynaa.moreoffhandslots.api.OffhandInventory;
import akkynaa.moreoffhandslots.network.PacketHandler;
import akkynaa.moreoffhandslots.network.message.PlayerOffhandPositionSyncMessage;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.PacketDistributor;

public class OffhandPosition {

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

    public void copyFrom(OffhandPosition other) {
        this.position = other.position;
    }

    public void updateData(Player player) {
        PacketHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) player),
                new PlayerOffhandPositionSyncMessage(position));
    }

    public CompoundTag serializeNBT() {
        CompoundTag nbt = new CompoundTag();
        nbt.putInt("position", position);
        return nbt;
    }

    public void deserializeNBT(CompoundTag nbt) {
        if (nbt.contains("position")) {
            position = nbt.getInt("position");
        }
    }

}
