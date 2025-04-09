package akkynaa.moreoffhandslots.network.payload;

import akkynaa.moreoffhandslots.capability.OffhandRegistry;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler;
import top.theillusivec4.curios.api.type.inventory.ICurioStacksHandler;
import top.theillusivec4.curios.api.type.inventory.IDynamicStackHandler;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public record CycleOffhandPayload(boolean next, boolean cycleEmptySlots) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<CycleOffhandPayload> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath("moreoffhandslots", "cycle_offhand"));

    public static final StreamCodec<RegistryFriendlyByteBuf, CycleOffhandPayload> STREAM_CODEC = new StreamCodec<>() {

        @Nonnull
        @Override
        public CycleOffhandPayload decode(RegistryFriendlyByteBuf buffer) {
            return new CycleOffhandPayload(buffer.readBoolean(), buffer.readBoolean());
        }

        @Override
        public void encode(RegistryFriendlyByteBuf buffer, CycleOffhandPayload value) {
            buffer.writeBoolean(value.next());
            buffer.writeBoolean(value.cycleEmptySlots());
        }
    };

    public static void handleServer(final CycleOffhandPayload data, final IPayloadContext context) {
        context.enqueueWork(() -> {
            Player player = context.player();
            cycleOffhandSlots(player, data.next(), data.cycleEmptySlots());
        });
    }

    public static void handleClient(final CycleOffhandPayload data, final IPayloadContext context) {
        context.enqueueWork(() -> {
            // Client-side handling is not needed for this operation
        });
    }

    private static void cycleOffhandSlots(Player player, boolean next, boolean cycleEmptySlots) {

        // Check if the player has a two-handed weapon equipped (Better Combat mod compatibility)
        if (isTwoHandedWeaponEquipped(player)) {
            return; // Do not cycle if a two-handed weapon is equipped
        }


        // Get the current offhand item
        ItemStack currentOffhandItem = player.getItemInHand(InteractionHand.OFF_HAND);

        // Get the player's curios capability
        Optional<ICuriosItemHandler> maybeCuriosInventory = CuriosApi.getCuriosInventory(player);

        if (maybeCuriosInventory.isPresent()) {

            ICuriosItemHandler curios = maybeCuriosInventory.get();
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

                    player.getData(OffhandRegistry.OFFHAND_POSITION.get()).changePosition(player, next ? loopCount : -loopCount);

                    // Update the offhand and curio slots with rotated items
                    player.setItemInHand(InteractionHand.OFF_HAND, allItems[0]);
                    for (int i = 0; i < slotCount; i++) {
                        stackHandler.setStackInSlot(i, allItems[i + 1]);
                    }
                }
            }
        }
    }

    private static boolean isTwoHandedWeaponEquipped(Player player) {
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

    @Nonnull
    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}