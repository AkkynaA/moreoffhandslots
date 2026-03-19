package net.akkynaa.moreoffhandslots.api;

import net.akkynaa.moreoffhandslots.capability.OffhandRegistry;
import net.akkynaa.moreoffhandslots.client.config.ClientConfig;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.akkynaa.slotlib.common.SlotLibRegistry;
import net.akkynaa.slotlib.common.capability.SlotLibInventory;
import java.util.ArrayList;
import java.util.List;

public class OffhandInventory {

    private static SlotLibInventory getSlotLibInventory(Player player) {
        return player.getData(SlotLibRegistry.INVENTORY);
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
            if (!item.isEmpty() || ClientConfig.CYCLE_EMPTY_SLOTS.get()) {
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
        int position = player.getData(OffhandRegistry.OFFHAND_POSITION).getPosition();
        ItemStack currentOffhandItem = player.getItemInHand(InteractionHand.OFF_HAND);
        int maxIterations = items.size();
        while (!items.get(position).equals(currentOffhandItem) && maxIterations-- > 0) {
            ItemStack first = items.getFirst();
            items.removeFirst();
            items.addLast(first);
        }
        return items;
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

       int position = player.getData(OffhandRegistry.OFFHAND_POSITION).getPosition();
       int renderPosition = 0;
       for (int i = 0; i < position; i++) {
           if (!itemsToRender.get(i).isEmpty()) {
               renderPosition += 1;
           }
       }
       return renderPosition;
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
}