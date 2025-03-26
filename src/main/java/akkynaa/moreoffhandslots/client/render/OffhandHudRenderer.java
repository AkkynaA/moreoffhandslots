package akkynaa.moreoffhandslots.client.render;

import akkynaa.moreoffhandslots.MoreOffhandSlots;
import akkynaa.moreoffhandslots.client.config.ClientConfig;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler;
import top.theillusivec4.curios.api.type.inventory.ICurioStacksHandler;
import top.theillusivec4.curios.api.type.inventory.IDynamicStackHandler;

import java.io.ObjectInputFilter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@OnlyIn(Dist.CLIENT)
public class OffhandHudRenderer {

    private static final ResourceLocation WIDGETS_LOCATION = ResourceLocation.fromNamespaceAndPath("minecraft", "hud/hotbar_offhand_left");

    public static void register(RegisterGuiLayersEvent event) {

        event.registerAbove(
                VanillaGuiLayers.HOTBAR,
                ResourceLocation.fromNamespaceAndPath(MoreOffhandSlots.MODID, "offhand_hud"),
                OffhandHudRenderer::renderOffhandHud
        );

    }
    public static void renderOffhandHud(GuiGraphics guiGraphics, DeltaTracker deltaTracker) {

        Minecraft minecraft = Minecraft.getInstance();
        Entity entity = minecraft.getCameraEntity();

        if (minecraft.options.hideGui || Objects.requireNonNull(minecraft.gameMode).getPlayerMode() == GameType.SPECTATOR) {
            return;
        }

        if (entity == null) {
            return;
        }

        if (!(entity instanceof LocalPlayer player)) {
            return;
        }

        int screenWidth = guiGraphics.guiWidth();
        int screenHeight = guiGraphics.guiHeight();

        List<ItemStack> cycleItems = getCycleItems(player);

        ItemStack currentItem = player.getItemInHand(InteractionHand.OFF_HAND);
        ItemStack nextItem = cycleItems.size() > 1 ? cycleItems.get(1) : cycleItems.get(0);
        ItemStack prevItem = cycleItems.getLast();

        if (!currentItem.isEmpty() || ClientConfig.RENDER_EMPTY_OFFHAND.get()) {
            renderThreeOffhandItems(guiGraphics, deltaTracker, player, screenWidth, screenHeight, prevItem, currentItem, nextItem);
        }
    }

    private static void renderThreeOffhandItems(GuiGraphics guiGraphics, DeltaTracker deltaTracker, LocalPlayer player, int screenWidth, int screenHeight,
                                                ItemStack prevItem, ItemStack currentItem, ItemStack nextItem) {

        final int ITEM_SIZE = 16;
        final int ITEM_SPACING = 5;
        final int TOTAL_ITEM_SPACE = ITEM_SIZE + ITEM_SPACING;
        final float SCALE = 0.90f;

        // Constants for hotbar positioning
        final int HOTBAR_WIDTH = 182; // Width of the hotbar in pixels
        final int HOTBAR_MARGIN = 25;  // Margin to add between hotbar and offhand slots

        // Position anchor for Y-coordinate
        int baseY = screenHeight - ITEM_SIZE - 3 + ClientConfig.Y_OFFSET.get();

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
            hotbarEdge = screenCenter + HOTBAR_WIDTH / 2 + HOTBAR_MARGIN - 15;
            // Position the item group so its left edge aligns with the hotbar's right edge
            middleX = hotbarEdge + totalWidth / 2 - ITEM_SIZE / 2;
        }

        // Calculate positions for all three items
        int prevX = middleX - TOTAL_ITEM_SPACE + ClientConfig.X_OFFSET.get();
        int currentX = middleX + ClientConfig.X_OFFSET.get();
        int nextX = middleX + TOTAL_ITEM_SPACE + ClientConfig.X_OFFSET.get();

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableDepthTest();
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

        // Draw the side offhand slots
        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(prevX, baseY, 0.0F);
        guiGraphics.pose().scale(SCALE, SCALE, 1.0F);
        guiGraphics.pose().translate(-prevX, -baseY, 0.0F);

        guiGraphics.blitSprite(WIDGETS_LOCATION, prevX + 1, baseY - 3, 29, 24);
        guiGraphics.blitSprite(WIDGETS_LOCATION, nextX, baseY - 3, 29, 24);

        guiGraphics.pose().popPose();

        // Draw the main offhand slot
        guiGraphics.pose().pushPose();

        guiGraphics.blitSprite(WIDGETS_LOCATION, currentX - 3, baseY - 4,29, 24);

        guiGraphics.pose().popPose();

        RenderSystem.disableBlend();


        renderItem(guiGraphics, currentX, baseY, deltaTracker, player, currentItem, true);

        RenderSystem.enableBlend();
        // greyed out side items
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 0.5F);

        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(prevX, baseY, 0.0F);
        guiGraphics.pose().scale(SCALE, SCALE, SCALE);
        guiGraphics.pose().translate(-prevX, -baseY, 0.0F);
        renderItem(guiGraphics, prevX+4, baseY+1, deltaTracker, player, prevItem, false);
        guiGraphics.pose().popPose();
        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(nextX, baseY, 0.0F);
        guiGraphics.pose().scale(SCALE, SCALE, SCALE);
        guiGraphics.pose().translate(-nextX, -baseY, 0.0F);
        renderItem(guiGraphics, nextX-2, baseY+1, deltaTracker, player, nextItem, false);
        guiGraphics.pose().popPose();
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.disableBlend();


    }

    private static void renderItem(GuiGraphics guiGraphics, int x, int y, DeltaTracker deltaTracker, Player player, ItemStack stack, boolean doDecoration) {
        if (!stack.isEmpty()) {
            float f = (float)stack.getPopTime() - deltaTracker.getGameTimeDeltaPartialTick(false);
            if (f > 0.0F) {
                float f1 = 1.0F + f / 5.0F;
                guiGraphics.pose().pushPose();
                guiGraphics.pose().translate((float)(x + 8), (float)(y + 12), 0.0F);
                guiGraphics.pose().scale(1.0F / f1, (f1 + 1.0F) / 2.0F, 1.0F);
                guiGraphics.pose().translate((float)(-(x + 8)), (float)(-(y + 12)), 0.0F);
            }

            guiGraphics.renderItem(player, stack, x, y, 0);
            if (f > 0.0F) {
                guiGraphics.pose().popPose();
            }

            if (doDecoration) {
                guiGraphics.renderItemDecorations(Minecraft.getInstance().font, stack, x, y);
            }
        }
    }


        private static List<ItemStack> getCycleItems(Player player) {
        List<ItemStack> items = new ArrayList<>();

        // Add current offhand
        ItemStack offhandItem = player.getItemInHand(InteractionHand.OFF_HAND);
        items.add(offhandItem);

        // Get curio items
        Optional<ICuriosItemHandler> maybeCuriosInventory = CuriosApi.getCuriosInventory(player);

        if (maybeCuriosInventory.isPresent()) {
            ICuriosItemHandler curios = maybeCuriosInventory.get();
            ICurioStacksHandler offhandSlots = curios.getCurios().get("offhand");

            if (offhandSlots != null) {
                IDynamicStackHandler stackHandler = offhandSlots.getStacks();
                int slotCount = offhandSlots.getSlots();

                for (int i = 0; i < slotCount; i++) {
                    ItemStack stack = stackHandler.getStackInSlot(i);
                    if (!stack.isEmpty() || ClientConfig.CYCLE_EMPTY_SLOTS.get()) {
                        items.add(stack);
                    }
                }
            }
        }

        return items;
    }
}