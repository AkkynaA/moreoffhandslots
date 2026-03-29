package akkynaa.moreoffhandslots.api;

import akkynaa.moreoffhandslots.capability.ModCapabilities;
import akkynaa.moreoffhandslots.client.config.ClientConfig;
import net.akkynaa.slotlib.common.capability.SlotLibCapabilityProvider;
import net.akkynaa.slotlib.common.capability.SlotLibInventory;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class OffhandInventory {

    private static SlotLibInventory getSlotLibInventory(Player player) {
        return player.getCapability(SlotLibCapabilityProvider.INVENTORY_CAP)
                .orElseGet(SlotLibInventory::new);
    }

    public static List<ItemStack> getOffhandItemsFromApi(Player player) {
        List<ItemStack> items = new ArrayList<>();
        SlotLibInventory inv = getSlotLibInventory(player);
        for (int i = 0; i < inv.getSlots(); i++) {
            items.add(inv.getStackInSlot(i));
        }
        return items;
    }

    public static ItemStackHandler getOffhandStackHandler(Player player) {
        return getSlotLibInventory(player).getStacks();
    }


    /**
     * Returns a list of items in the offhand not including empty items, plus the current item in hand.
     * The current item in hand is always the first item in the list, followed by the items
     * from the SlotLib slots.
     * This method is used for rendering the offhand items in the HUD.
     *
     * @param player The player to get the offhand items from.
     * @return A list of all items in the offhand.
     */
    public static List<ItemStack> getOffhandItemsToRender(Player player) {
        List<ItemStack> allItems = new ArrayList<>();
        ItemStack currentOffhandItem = player.getItemInHand(InteractionHand.OFF_HAND);
        allItems.add(currentOffhandItem);

        List<ItemStack> slotLibItems = getOffhandItemsFromApi(player);

        for (ItemStack item : slotLibItems) {
            if (!item.isEmpty() || ClientConfig.EMPTY_SLOT_BEHAVIOR.get() != ClientConfig.EmptySlotBehavior.SKIP) {
                allItems.add(item);
            }
        }
        return allItems;
    }

    /**
     * Returns a list of all items in the offhand, including the current item in hand.
     * The current item in hand is always the first item in the list, followed by the items
     * from the SlotLib slots.
     *
     * @param player The player to get the offhand items from.
     * @return A list of all items in the offhand.
     */
    public static List<ItemStack> getAllOffhandItems(Player player) {
        List<ItemStack> allItems = new ArrayList<>();
        ItemStack currentOffhandItem = player.getItemInHand(InteractionHand.OFF_HAND);
        allItems.add(currentOffhandItem);

        List<ItemStack> slotLibItems = getOffhandItemsFromApi(player);

        allItems.addAll(slotLibItems);

        return allItems;
    }
    /**
     * Returns a list of all items in the offhand, including the current item in hand.
     * The items are ordered in the same way as they appear in the hotbar style renderer,
     * with the current item at the position of the selection indicator, and the rest of the items
     * shifted by that amount.
     *
     *
     * @param player The player to get the offhand items from.
     * @return A list of all items in the offhand.
     */
    public static List<ItemStack> getAllOffhandItemsInOrder(Player player) {
        var items = getAllOffhandItems(player);
        player.getCapability(ModCapabilities.OFFHAND_POSITION).ifPresent(offhandPosition -> {
            int position = offhandPosition.getPosition();
            ItemStack currentOffhandItem = player.getItemInHand(InteractionHand.OFF_HAND);
            int maxIterations = items.size();
            while (!items.get(position).equals(currentOffhandItem) && maxIterations-- > 0) {
                ItemStack first = items.get(0);
                items.remove(0);
                items.add(first);
            }
        });
        return items;
    }

    public static int getPosition(Player player) {
        AtomicInteger position = new AtomicInteger();
        player.getCapability(ModCapabilities.OFFHAND_POSITION).ifPresent(offhandPosition ->
                position.set(offhandPosition.getPosition()));
        return position.get();
    }

    /**
     * Returns the position that the selection indicator should be rendered at
     * in the hotbar style renderer, when RENDER_EMPTY_SLOTS is disabled.
     *
     * @param player The player to get the offhand items from.
     * @return The position of the selection indicator.
     */
    public static int getRenderPosition(Player player) {

        var itemsToRender = getAllOffhandItemsInOrder(player);

        AtomicInteger renderPosition = new AtomicInteger();
        player.getCapability(ModCapabilities.OFFHAND_POSITION).ifPresent(offhandPosition -> {
            int position = offhandPosition.getPosition();
            for (int i = 0; i < position; i++) {
                if (!itemsToRender.get(i).isEmpty()) {
                    renderPosition.addAndGet(1);
                }
            }
        });
        return renderPosition.get();
    }

    /**
     * Returns the number of items in the offhand, including the current item in hand.
     * The current item in hand is always included in the count.
     *
     * @param player The player to get the offhand items from.
     * @return The number of items in the offhand.
     */
    public static int getOffhandItemsLength(Player player) {
        return getSlotLibInventory(player).getSlots() + 1; // +1 for the vanilla offhand slot
    }

    /**
     * Collapses consecutive empty slots in the item list into a single empty slot.
     * Handles circular wrap-around: if both the first and last items are empty,
     * they are considered part of the same series and the trailing empty is removed.
     *
     * @param items The list of items to collapse.
     * @return A new list with consecutive empties collapsed.
     */
    public static List<ItemStack> collapseConsecutiveEmpties(List<ItemStack> items) {
        if (items.size() <= 1) return new ArrayList<>(items);

        List<ItemStack> collapsed = new ArrayList<>();
        collapsed.add(items.get(0));

        boolean lastWasEmpty = items.get(0).isEmpty();
        for (int i = 1; i < items.size(); i++) {
            ItemStack item = items.get(i);
            if (item.isEmpty()) {
                if (!lastWasEmpty) {
                    collapsed.add(item);
                }
                lastWasEmpty = true;
            } else {
                collapsed.add(item);
                lastWasEmpty = false;
            }
        }

        // Handle circular wrap: if first and last are both empty, they form one series
        if (collapsed.size() > 1 && collapsed.get(0).isEmpty() && collapsed.get(collapsed.size() - 1).isEmpty()) {
            collapsed.remove(collapsed.size() - 1);
        }

        return collapsed;
    }
}
