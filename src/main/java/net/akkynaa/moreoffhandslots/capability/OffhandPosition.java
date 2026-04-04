package net.akkynaa.moreoffhandslots.capability;

import net.akkynaa.moreoffhandslots.api.OffhandInventory;
import net.akkynaa.moreoffhandslots.network.payload.PlayerOffhandPositionSyncPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.common.util.ValueIOSerializable;
import net.neoforged.neoforge.network.PacketDistributor;
import javax.annotation.Nonnull;

public class OffhandPosition implements ValueIOSerializable {
    private Integer position = 0;
    private boolean dirty = false;

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
        this.dirty = true;
    }

    public void changePosition(Player player, int change) {
        int offhandItemsLength = OffhandInventory.getOffhandItemsLength(player);
        if (offhandItemsLength == 0) return;
        position = (position + change + offhandItemsLength) % offhandItemsLength;
        this.dirty = true;
    }

    public void updateData(Player player) {
        if (!dirty) return;
        dirty = false;
        PacketDistributor.sendToPlayer((ServerPlayer) player,
                new PlayerOffhandPositionSyncPayload(position));
    }

    @Override
    public void serialize(@Nonnull ValueOutput output) {
        output.putInt("position", position);
    }

    @Override
    public void deserialize(@Nonnull ValueInput input) {
        this.position = input.getIntOr("position", 0);
        this.dirty = true;
    }
}