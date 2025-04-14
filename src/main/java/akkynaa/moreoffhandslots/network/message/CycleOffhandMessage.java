package akkynaa.moreoffhandslots.network.message;

import akkynaa.moreoffhandslots.api.OffhandInventory;
import akkynaa.moreoffhandslots.capability.ModCapabilities;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class CycleOffhandMessage {
    private final boolean next; // true = next, false = previous
    private final boolean cycleEmptySlots;

    public CycleOffhandMessage(boolean next, boolean cycleEmptySlots) {
        this.next = next;
        this.cycleEmptySlots = cycleEmptySlots;
    }

    public static void encode(CycleOffhandMessage message, FriendlyByteBuf buffer) {
        buffer.writeBoolean(message.next);
        buffer.writeBoolean(message.cycleEmptySlots);
    }

    public static CycleOffhandMessage decode(FriendlyByteBuf buffer) {
        return new CycleOffhandMessage(buffer.readBoolean(), buffer.readBoolean());
    }

    public static void handle(CycleOffhandMessage message, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player != null) {
                cycleOffhandSlots(player, message.next, message.cycleEmptySlots);
            }
        });
        context.setPacketHandled(true);
    }

    private static void cycleOffhandSlots(ServerPlayer player, boolean next, boolean cycleEmptySlots) {

        // Check if the player has a two-handed weapon equipped (Better Combat mod compatibility)
        if (isTwoHandedWeaponEquipped(player)) {
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

            int loopCount = 0;
            do {
                cycleSingleStep(allItems, next);
                loopCount++;

                if (cycleEmptySlots || loopCount >= allItems.length)
                    break;

            } while (allItems[0].isEmpty());


            var stackHandler = OffhandInventory.getOffhandStackHandler(player);

            if (stackHandler == null) {
                return;
            }

            int finalLoopCount = loopCount;
            player.getCapability(ModCapabilities.OFFHAND_POSITION).ifPresent(offhandPosition ->
                    offhandPosition.changePosition(player, next ? finalLoopCount: -finalLoopCount));
            player.setItemInHand(InteractionHand.OFF_HAND, allItems[0]);
            for (int i = 0; i < extraSlotItems.size(); i++) {
                stackHandler.setStackInSlot(i, allItems[i + 1]);
            }
        }


    }

    /**
     * Checks if the player has a two-handed weapon equipped.
     * This is a compatibility check for Better Combat mod.
     *
     * @param player The player to check
     * @return true if a two-handed weapon is equipped
     */
    private static boolean isTwoHandedWeaponEquipped(ServerPlayer player) {
        // Use the compatibility layer to check if a two-handed weapon is equipped
        return akkynaa.moreoffhandslots.compat.BetterCombatCompat.hasTwoHandedWeaponEquipped(player);
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