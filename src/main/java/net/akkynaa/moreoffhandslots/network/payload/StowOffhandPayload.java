package net.akkynaa.moreoffhandslots.network.payload;

import net.akkynaa.moreoffhandslots.api.OffhandInventory;
import net.akkynaa.moreoffhandslots.capability.OffhandRegistry;
import net.akkynaa.moreoffhandslots.compat.BetterCombatCompat;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import javax.annotation.Nonnull;

public record StowOffhandPayload() implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<StowOffhandPayload> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath("moreoffhandslots", "stow_offhand"));

    public static final StreamCodec<RegistryFriendlyByteBuf, StowOffhandPayload> STREAM_CODEC =
            StreamCodec.unit(new StowOffhandPayload());

    public static void handleServer(final StowOffhandPayload data, final IPayloadContext context) {
        context.enqueueWork(() -> stowOffhandItem(context.player()));
    }

    private static void stowOffhandItem(Player player) {
        if (BetterCombatCompat.hasTwoHandedWeaponEquipped(player)) return;

        ItemStack currentOffhandItem = player.getItemInHand(InteractionHand.OFF_HAND);
        if (currentOffhandItem.isEmpty()) return;

        var extraSlotItems = OffhandInventory.getOffhandItemsFromApi(player);
        int firstEmptySlot = -1;
        for (int i = 0; i < extraSlotItems.size(); i++) {
            if (extraSlotItems.get(i).isEmpty()) {
                firstEmptySlot = i;
                break;
            }
        }

        if (firstEmptySlot < 0) return;

        int steps = firstEmptySlot + 1;
        ItemStack[] allItems = new ItemStack[extraSlotItems.size() + 1];
        allItems[0] = currentOffhandItem;
        for (int i = 0; i < extraSlotItems.size(); i++) {
            allItems[i + 1] = extraSlotItems.get(i);
        }

        for (int i = 0; i < steps; i++) {
            ItemStack firstItem = allItems[0];
            for (int j = 0; j < allItems.length - 1; j++) {
                allItems[j] = allItems[j + 1];
            }
            allItems[allItems.length - 1] = firstItem;
        }

        var stackHandler = OffhandInventory.getOffhandStackHandler(player);
        player.setItemInHand(InteractionHand.OFF_HAND, allItems[0]);
        for (int i = 0; i < extraSlotItems.size(); i++) {
            stackHandler.setStackInSlot(i, allItems[i + 1]);
        }
        player.getData(OffhandRegistry.OFFHAND_POSITION).changePosition(player, steps);
    }

    @Nonnull
    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
