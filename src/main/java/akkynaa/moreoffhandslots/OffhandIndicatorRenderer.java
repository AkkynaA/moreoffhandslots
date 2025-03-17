package akkynaa.moreoffhandslots;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler;
import top.theillusivec4.curios.api.type.inventory.ICurioStacksHandler;
import top.theillusivec4.curios.api.type.inventory.IDynamicStackHandler;

import java.io.Console;
import java.util.ArrayList;
import java.util.List;

@Mod.EventBusSubscriber(modid = MoreOffhandSlots.MODID)
public class OffhandIndicatorRenderer {

    private static final ResourceLocation WIDGETS_LOCATION = ResourceLocation.fromNamespaceAndPath("minecraft", "textures/gui/widgets.png");


    @SubscribeEvent
    public static void onRenderOverlayPost(RenderGuiOverlayEvent.Post event) {
        if (event.getOverlay() != VanillaGuiOverlay.HOTBAR.type()) {
            return;
        }

        Minecraft minecraft = Minecraft.getInstance();
        LocalPlayer player = minecraft.player;

        if (player == null) {
            return;
        }

        GuiGraphics guiGraphics = event.getGuiGraphics();
        int screenWidth = event.getWindow().getGuiScaledWidth();
        int screenHeight = event.getWindow().getGuiScaledHeight();

        List<ItemStack> cycleItems = getCycleItems(player);

        ItemStack currentItem = player.getItemInHand(InteractionHand.OFF_HAND);
        ItemStack nextItem = cycleItems.size() > 1 ? cycleItems.get(1) : cycleItems.get(0);
        ItemStack prevItem = cycleItems.get(cycleItems.size()-1);

        if (!currentItem.isEmpty() || (currentItem.isEmpty() && Config.renderEmptyOffhand)) {
            renderThreeOffhandItems(guiGraphics, player, screenWidth, screenHeight, prevItem, currentItem, nextItem);
        }
    }

    private static void renderThreeOffhandItems(GuiGraphics guiGraphics, LocalPlayer player, int screenWidth, int screenHeight,
                                                ItemStack prevItem, ItemStack currentItem, ItemStack nextItem) {

        final int ITEM_SIZE = 16;
        final int ITEM_SPACING = 5;
        final int TOTAL_ITEM_SPACE = ITEM_SIZE + ITEM_SPACING;
        final float SCALE = 0.90f;

        // Constants for hotbar positioning
        final int HOTBAR_WIDTH = 182; // Width of the hotbar in pixels
        final int HOTBAR_MARGIN = 25;  // Margin to add between hotbar and offhand slots

        // Position anchor for Y-coordinate
        int baseY = screenHeight - ITEM_SIZE - 3;

        // Determine which side to render on based on main arm
        HumanoidArm arm = player.getMainArm();
        boolean rightHanded = (arm == HumanoidArm.RIGHT);

        // Calculate total width needed for 3 items
        int totalWidth = 3 * ITEM_SIZE + 2 * ITEM_SPACING;

        int screenCenter = screenWidth / 2;

        // Calculate the edge of the hotbar
        int hotbarEdge;
        int middleX;
        if (rightHanded) {
            hotbarEdge = screenCenter - HOTBAR_WIDTH / 2 - HOTBAR_MARGIN;
            middleX = hotbarEdge - totalWidth / 2 + ITEM_SIZE / 2;
        } else {
            // For left-handed, place on the right side of the hotbar
            hotbarEdge = screenCenter + HOTBAR_WIDTH / 2 + HOTBAR_MARGIN;
            // Position the item group so its left edge aligns with the hotbar's right edge
            middleX = hotbarEdge + totalWidth / 2 - ITEM_SIZE / 2;
        }

        // Calculate positions for all three items
        int prevX = middleX - TOTAL_ITEM_SPACE;
        int currentX = middleX;
        int nextX = middleX + TOTAL_ITEM_SPACE;

        PoseStack matrix = guiGraphics.pose();

        // Draw prev item background
        matrix.pushPose();
        matrix.translate(prevX, baseY, 0);
        matrix.scale(SCALE, SCALE, SCALE);
        matrix.translate(-prevX, -baseY, 0);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShaderTexture(0, WIDGETS_LOCATION);
        guiGraphics.blit(WIDGETS_LOCATION, prevX + 1, baseY - 2, 24, 23, ITEM_SIZE + 7, ITEM_SIZE + 7);
        RenderSystem.disableBlend();
        matrix.popPose();


        // Draw next item background
        matrix.pushPose();
        matrix.translate(nextX, baseY, 0);
        matrix.scale(SCALE, SCALE, SCALE);
        matrix.translate(-nextX, -baseY, 0);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShaderTexture(0, WIDGETS_LOCATION);
        guiGraphics.blit(WIDGETS_LOCATION, nextX - 5, baseY - 2, 24, 23, ITEM_SIZE + 7, ITEM_SIZE + 7);
        RenderSystem.disableBlend();
        matrix.popPose();


        // Draw current item background
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShaderTexture(0, WIDGETS_LOCATION);
        guiGraphics.blit(WIDGETS_LOCATION, currentX - 3, baseY - 3, 24, 23, ITEM_SIZE + 7, ITEM_SIZE + 7);
        RenderSystem.disableBlend();


        // Draw the current item
        guiGraphics.renderItem(currentItem, currentX, baseY);
        guiGraphics.renderItemDecorations(Minecraft.getInstance().font, currentItem, currentX, baseY);

        // Draw the prev item
        if (!prevItem.isEmpty()) {
            matrix.pushPose();
            matrix.translate(prevX, baseY, 0);
            matrix.scale(SCALE, SCALE, SCALE);
            matrix.translate(-prevX, -baseY, 0);
            guiGraphics.renderItem(prevItem, prevX + 4, baseY + 1);
            matrix.popPose();
        }

        // Draw the next item
        if (!nextItem.isEmpty()) {
            matrix.pushPose();
            matrix.translate(nextX, baseY, 0);
            matrix.scale(SCALE, SCALE, SCALE);
            matrix.translate(-nextX, -baseY, 0);
            guiGraphics.renderItem(nextItem, nextX - 2, baseY + 1);
            matrix.popPose();
        }
    }

    private static List<ItemStack> getCycleItems(Player player) {
        List<ItemStack> items = new ArrayList<>();

        // Add current offhand
        ItemStack offhandItem = player.getItemInHand(InteractionHand.OFF_HAND);
        items.add(offhandItem);

        // Get curio items
        LazyOptional<ICuriosItemHandler> maybeCuriosInventory = CuriosApi.getCuriosInventory(player);

        if (maybeCuriosInventory.isPresent()) {
            ICuriosItemHandler curios = maybeCuriosInventory.resolve().get();
            ICurioStacksHandler offhandSlots = curios.getCurios().get("offhand");

            if (offhandSlots != null) {
                IDynamicStackHandler stackHandler = offhandSlots.getStacks();
                int slotCount = offhandSlots.getSlots();

                for (int i = 0; i < slotCount; i++) {
                    ItemStack stack = stackHandler.getStackInSlot(i);
                    if (!stack.isEmpty() || Config.cycleEmptySlots) {
                        items.add(stack);
                    }
                }
            }
        }

        return items;
    }
}