package akkynaa.moreoffhandslots.api;

import akkynaa.moreoffhandslots.capability.OffhandRegistry;
import akkynaa.moreoffhandslots.client.config.ClientConfig;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.type.inventory.ICurioStacksHandler;
import top.theillusivec4.curios.api.type.inventory.IDynamicStackHandler;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class OffhandInventory {

    private static Optional<ICurioStacksHandler> getOffhandSlotsHandler(Player player) {
        return CuriosApi.getCuriosInventory(player)
                .map(c -> c.getCurios().get("offhand"));
    }

    public static List<ItemStack> getOffhandItemsFromApi(Player player) {
        List<ItemStack> curiosItems = new ArrayList<>();
        getOffhandSlotsHandler(player).ifPresent(offhandSlots -> {
            IDynamicStackHandler stackHandler = offhandSlots.getStacks();
            for (int i = 0; i < offhandSlots.getSlots(); i++) {
                curiosItems.add(stackHandler.getStackInSlot(i));
            }
        });
        return curiosItems;
    }

    public static IDynamicStackHandler getOffhandStackHandler(Player player) {
        return getOffhandSlotsHandler(player).map(ICurioStacksHandler::getStacks).orElse(null);
    }


    /**
     * Returns a list of items in the offhand not including empty items, plus the current item in hand.
     * The current item in hand is always the first item in the list, followed by the items
     * from the Curios slots.
     * This method is used for rendering the offhand items in the HUD.
     *
     * @param player The player to get the offhand items from.
     * @return A list of all items in the offhand.
     */
    public static List<ItemStack> getOffhandItemsToRender(Player player) {
        List<ItemStack> allItems = new ArrayList<>();
        ItemStack currentOffhandItem = player.getItemInHand(InteractionHand.OFF_HAND);
        allItems.add(currentOffhandItem);

        List<ItemStack> curiosItems = getOffhandItemsFromApi(player);

        for (ItemStack item : curiosItems) {
            if (!item.isEmpty() || ClientConfig.CYCLE_EMPTY_SLOTS.get()) {
                allItems.add(item);
            }
        }
        return allItems;
    }

    /**
     * Returns a list of all items in the offhand, including the current item in hand.
     * The current item in hand is always the first item in the list, followed by the items
     * from the Curios slots.
     *
     * @param player The player to get the offhand items from.
     * @return A list of all items in the offhand.
     */
    public static List<ItemStack> getAllOffhandItems(Player player) {
        List<ItemStack> allItems = new ArrayList<>();
        ItemStack currentOffhandItem = player.getItemInHand(InteractionHand.OFF_HAND);
        allItems.add(currentOffhandItem);

        List<ItemStack> curiosItems = getOffhandItemsFromApi(player);

        allItems.addAll(curiosItems);

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
        return getOffhandSlotsHandler(player)
                .map(offhandSlots -> offhandSlots.getSlots() + 1) // +1 for the vanilla offhand slot
                .orElse(0);
    }
}