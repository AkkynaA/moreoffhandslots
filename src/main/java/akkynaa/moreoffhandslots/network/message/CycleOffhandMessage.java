package akkynaa.moreoffhandslots.network.message;

import akkynaa.moreoffhandslots.api.OffhandInventory;
import akkynaa.moreoffhandslots.capability.ModCapabilities;
import akkynaa.moreoffhandslots.client.config.ClientConfig;
import akkynaa.moreoffhandslots.compat.BetterCombatCompat;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class CycleOffhandMessage {
    private final boolean next; // true = next, false = previous
    private final int emptySlotBehavior;

    public CycleOffhandMessage(boolean next, int emptySlotBehavior) {
        this.next = next;
        this.emptySlotBehavior = emptySlotBehavior;
    }

    public static void encode(CycleOffhandMessage message, FriendlyByteBuf buffer) {
        buffer.writeBoolean(message.next);
        buffer.writeInt(message.emptySlotBehavior);
    }

    public static CycleOffhandMessage decode(FriendlyByteBuf buffer) {
        return new CycleOffhandMessage(buffer.readBoolean(), buffer.readInt());
    }

    public static void handle(CycleOffhandMessage message, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player != null) {
                ClientConfig.EmptySlotBehavior behavior = ClientConfig.EmptySlotBehavior.values()[message.emptySlotBehavior];
                cycleOffhandSlots(player, message.next, behavior);
            }
        });
        context.setPacketHandled(true);
    }

    private static void cycleOffhandSlots(ServerPlayer player, boolean next, ClientConfig.EmptySlotBehavior emptySlotBehavior) {

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

            int finalLoopCount = loopCount;
            player.getCapability(ModCapabilities.OFFHAND_POSITION).ifPresent(offhandPosition ->
                    offhandPosition.changePosition(player, next ? finalLoopCount : -finalLoopCount));

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
}
