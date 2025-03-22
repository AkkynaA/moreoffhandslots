package akkynaa.moreoffhandslots.network.message;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.network.NetworkEvent;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler;
import top.theillusivec4.curios.api.type.inventory.ICurioStacksHandler;
import top.theillusivec4.curios.api.type.inventory.IDynamicStackHandler;

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
        // Get the current offhand item
        ItemStack currentOffhandItem = player.getItemInHand(InteractionHand.OFF_HAND);

        // Get the player's curios capability
        LazyOptional<ICuriosItemHandler> maybeCuriosInventory = CuriosApi.getCuriosInventory(player);

        if (maybeCuriosInventory.isPresent()) {

            ICuriosItemHandler curios = maybeCuriosInventory.resolve().get();
            ICurioStacksHandler offhandSlots = curios.getCurios().get("offhand");

            if (offhandSlots != null) {
                int slotCount = offhandSlots.getSlots();

                if (slotCount > 0) {
                    IDynamicStackHandler stackHandler = offhandSlots.getStacks();

                    // Create an array to hold all items including the offhand
                    ItemStack[] allItems = new ItemStack[slotCount + 1];

                    // Store the current offhand item at position 0
                    allItems[0] = currentOffhandItem;

                    // Store all curio slot items
                    for (int i = 0; i < slotCount; i++) {
                        allItems[i + 1] = stackHandler.getStackInSlot(i);
                    }


                    int loopCount = 0;
                    do {
                        cycleSingleStep(allItems, next);
                        loopCount++;

                        if (cycleEmptySlots || loopCount >= allItems.length)
                            break;

                    } while (allItems[0].isEmpty());

                    // Update the offhand and curio slots with rotated items
                    player.setItemInHand(InteractionHand.OFF_HAND, allItems[0]);
                    for (int i = 0; i < slotCount; i++) {
                        stackHandler.setStackInSlot(i, allItems[i + 1]);
                    }
                }
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