package net.akkynaa.moreoffhandslots.network.payload;

import net.akkynaa.moreoffhandslots.api.OffhandInventory;
import net.akkynaa.moreoffhandslots.capability.OffhandRegistry;
import net.akkynaa.moreoffhandslots.client.config.ClientConfig;
import net.akkynaa.moreoffhandslots.compat.BetterCombatCompat;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import javax.annotation.Nonnull;


public record CycleOffhandPayload(boolean next, int emptySlotBehavior) implements CustomPacketPayload {

    public static final Type<CycleOffhandPayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath("moreoffhandslots", "cycle_offhand"));

    public static final StreamCodec<RegistryFriendlyByteBuf, CycleOffhandPayload> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.BOOL, CycleOffhandPayload::next,
                    ByteBufCodecs.INT, CycleOffhandPayload::emptySlotBehavior,
                    CycleOffhandPayload::new
            );

    public static void handleServer(final CycleOffhandPayload data, final IPayloadContext context) {
        context.enqueueWork(() -> {
            Player player = context.player();
            ClientConfig.EmptySlotBehavior behavior = ClientConfig.EmptySlotBehavior.values()[data.emptySlotBehavior()];
            cycleOffhandSlots(player, data.next(), behavior);
        });
    }

    private static void cycleOffhandSlots(Player player, boolean next, ClientConfig.EmptySlotBehavior emptySlotBehavior) {

        // Check if the player has a two-handed weapon equipped (Better Combat mod compatibility)
        if (BetterCombatCompat.hasTwoHandedWeaponEquipped(player)) {
            return;
        }

        ItemStack currentOffhandItem = player.getItemInHand(InteractionHand.OFF_HAND);
        var extraSlotItems = OffhandInventory.getOffhandItemsFromApi(player);

        if (!extraSlotItems.isEmpty()) {

            ItemStack[] allItems = new ItemStack[extraSlotItems.size() + 1];

            allItems[0] = currentOffhandItem;

            for (int i = 0; i < extraSlotItems.size(); i++) {
                allItems[i + 1] = extraSlotItems.get(i);
            }

            boolean startedOnEmpty = allItems[0].isEmpty();
            int loopCount = 0;
            do {
                cycleSingleStep(allItems, next);
                loopCount++;

                if (loopCount >= allItems.length)
                    break;

                if (emptySlotBehavior == ClientConfig.EmptySlotBehavior.SKIP) {
                    // Skip all empty slots
                    if (!allItems[0].isEmpty()) break;
                    continue;
                }

                // When collapsing, skip consecutive empties (but keep the first one in a run)
                if (emptySlotBehavior == ClientConfig.EmptySlotBehavior.COLLAPSE && startedOnEmpty && allItems[0].isEmpty()) {
                    continue;
                }

                break;

            } while (true);


            var stackHandler = OffhandInventory.getOffhandStackHandler(player);

            if (stackHandler == null) {
                return;
            }

            player.getData(OffhandRegistry.OFFHAND_POSITION).changePosition(player, next ? loopCount : -loopCount);

            player.setItemInHand(InteractionHand.OFF_HAND, allItems[0]);
            for (int i = 0; i < extraSlotItems.size(); i++) {
                stackHandler.setStackInSlot(i, allItems[i + 1]);
            }
        }


    }

    /*
     * Rotates the items in the array by one position in the specified direction.
     * */
    private static void cycleSingleStep(ItemStack[] allItems, boolean next) {
        if (next) {
            ItemStack firstItem = allItems[0];
            for (int i = 0; i < allItems.length - 1; i++) {
                allItems[i] = allItems[i + 1];
            }
            allItems[allItems.length - 1] = firstItem;
        } else {
            ItemStack lastItem = allItems[allItems.length - 1];
            for (int i = allItems.length - 1; i > 0; i--) {
                allItems[i] = allItems[i - 1];
            }
            allItems[0] = lastItem;
        }
    }

    @Nonnull
    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}