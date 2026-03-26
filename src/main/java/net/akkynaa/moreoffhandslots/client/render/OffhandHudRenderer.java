package net.akkynaa.moreoffhandslots.client.render;

import net.akkynaa.moreoffhandslots.MoreOffhandSlots;
import net.akkynaa.moreoffhandslots.api.IOffhandHudRenderer;
import net.akkynaa.moreoffhandslots.api.OffhandInventory;
import net.akkynaa.moreoffhandslots.capability.OffhandRegistry;
import net.akkynaa.moreoffhandslots.client.config.ClientConfig;
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
import java.util.List;
import java.util.Objects;

@OnlyIn(Dist.CLIENT)
public final class OffhandHudRenderer implements IOffhandHudRenderer {

    private static final ResourceLocation OFFHAND_LOCATION = ResourceLocation.fromNamespaceAndPath("minecraft", "hud/hotbar_offhand_left");
    private static final ResourceLocation HOTBAR_LOCATION = ResourceLocation.fromNamespaceAndPath("minecraft", "hud/hotbar");
    private static final ResourceLocation SELECTION_LOCATION = ResourceLocation.fromNamespaceAndPath("minecraft", "hud/hotbar_selection");


    // Constants
    static final int ITEM_SIZE = 16;
    static final int SLOT_WIDTH = 20; // Width of a single hotbar slot
    static final int SLOT_HEIGHT = 22; // Height of a single hotbar slot
    static final int HOTBAR_WIDTH = 182; // Width of the hotbar in pixels
    static final int ITEM_SPACING = 5;
    static final int TOTAL_ITEM_SPACE = ITEM_SIZE + ITEM_SPACING;
    static final float SCALE = 0.90f;
    static final int HOTBAR_MARGIN = 25;

    // Hotbar texture source coordinates
    private static final int HOTBAR_TEXTURE_W = 182;
    private static final int HOTBAR_TEXTURE_H = 22;
    private static final int HOTBAR_FIRST_SLOT_BODY_SRC_X = 1;
    private static final int HOTBAR_MID_SLOT_SRC_X = 4 * SLOT_WIDTH; // = 80
    private static final int HOTBAR_LAST_SLOT_SRC_X = 160;

    private static int hotbarOffset = 0;

    public static int getHotbarOffset() {
        return hotbarOffset;
    }

    private static void setHotbarOffset(int offset) {
        hotbarOffset = offset;
    }


    private static IOffhandHudRenderer instance = new OffhandHudRenderer();

    public static void setOffhandRenderer(IOffhandHudRenderer offhandHudRenderer) {
        Objects.requireNonNull(offhandHudRenderer, "OffhandHudRenderer cannot be null");
        OffhandHudRenderer.instance = offhandHudRenderer;
    }

    public static IOffhandHudRenderer getOffhandRenderer() {
        return instance;
    }

    @Override
    public void renderOffhandHud(GuiGraphics guiGraphics, DeltaTracker deltaTracker) {
        Minecraft mc = Minecraft.getInstance();

        var style = ClientConfig.INDICATOR_STYLE.get();
        if (style == ClientConfig.IndicatorStyle.VANILLA) return;

        if (mc.options.hideGui) return;

        var gameMode = Objects.requireNonNull(mc.gameMode);
        if (gameMode.getPlayerMode() == GameType.SPECTATOR) return;

        Entity entity = mc.getCameraEntity();
        if (!(entity instanceof LocalPlayer player)) return;

        if (net.akkynaa.moreoffhandslots.compat.BetterCombatCompat.hasTwoHandedWeaponEquipped(player)) return;

        List<ItemStack> cycleItems = OffhandInventory.getOffhandItemsToRender(player);
        if (cycleItems.isEmpty()) return;

        ItemStack currentItem = player.getItemInHand(InteractionHand.OFF_HAND);
        if (currentItem.isEmpty() && !ClientConfig.RENDER_EMPTY_OFFHAND.get()) return;

        int screenWidth = guiGraphics.guiWidth();
        int screenHeight = guiGraphics.guiHeight();

        var emptyBehavior = ClientConfig.EMPTY_SLOT_BEHAVIOR.get();

        switch (style) {
            case DEFAULT, DETAILED -> {
                List<ItemStack> processedItems = cycleItems;

                // Collapse ONLY for these styles
                if (emptyBehavior == ClientConfig.EmptySlotBehavior.COLLAPSE) {
                    processedItems = OffhandInventory.collapseConsecutiveEmpties(processedItems);
                }

                ItemStack nextItem = processedItems.size() > 1
                        ? processedItems.get(1)
                        : processedItems.get(0);
                ItemStack prevItem = processedItems.getLast();

                if (style == ClientConfig.IndicatorStyle.DEFAULT) {
                    renderDefaultStyleOffhand(guiGraphics, deltaTracker, player,
                            screenWidth, screenHeight, prevItem, currentItem, nextItem);
                } else {
                    renderDetailedStyleOffhand(guiGraphics, deltaTracker, player,
                            screenWidth, screenHeight, prevItem, currentItem, nextItem);
                }
            }

            case HOTBAR -> renderHotbarStyleOffhand(guiGraphics, deltaTracker, player,
                    screenWidth, screenHeight, cycleItems);
        }
    }

    @Override
    public void renderHotbarStyleOffhand(GuiGraphics guiGraphics, DeltaTracker deltaTracker, LocalPlayer player,
                                                 int screenWidth, int screenHeight, List<ItemStack> items) {

        // Position anchor for Y-coordinate
        int baseY = screenHeight - ITEM_SIZE - 6 + ClientConfig.Y_OFFSET.get();

        // Determine which side to render on based on main arm
        HumanoidArm arm = player.getMainArm();
        boolean rightHanded = (arm == HumanoidArm.RIGHT);

        int renderSize = items.size();

        // Calculate total width needed for all items
        int totalWidth = renderSize * SLOT_WIDTH;

        int screenCenter = screenWidth / 2;

        // Calculate the position of the offhand bar
        int hotbarX;
        if (rightHanded) {
            if (ClientConfig.ALIGN_TO_CENTER.get()) {
                hotbarX = (screenWidth - totalWidth - 10 - HOTBAR_WIDTH) / 2 + ClientConfig.X_OFFSET.get();
            }
            else {
                hotbarX = screenCenter - HOTBAR_WIDTH / 2 - 10 - totalWidth + ClientConfig.X_OFFSET.get();
            }
        } else {
            if (ClientConfig.ALIGN_TO_CENTER.get()) {
                hotbarX = screenWidth - totalWidth - ((screenWidth - totalWidth - HOTBAR_WIDTH) / 2) + 3 + ClientConfig.X_OFFSET.get();
            }
            else {
                hotbarX = screenCenter + HOTBAR_WIDTH / 2 + 10 + ClientConfig.X_OFFSET.get();
            }
        }

        if (ClientConfig.ALIGN_TO_CENTER.get()) {
            if (rightHanded) {
                setHotbarOffset(
                        ((screenWidth - totalWidth + 10 - HOTBAR_WIDTH) / 2) + totalWidth + HOTBAR_WIDTH/2
                );
            } else {
                setHotbarOffset(
                        ((screenWidth - totalWidth - 10 - HOTBAR_WIDTH) / 2) + HOTBAR_WIDTH/2
                );
            }
        }

        // Draw the background
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableDepthTest();

        // Draw the hotbar background
        // For each slot, we draw a slice of the hotbar texture
        for (int i = 0; i < renderSize; i++) {
            int slotX = hotbarX + i * SLOT_WIDTH;

            if (i == 0) {
                // Left edge (1px)
                guiGraphics.blitSprite(HOTBAR_LOCATION,
                        HOTBAR_TEXTURE_W, HOTBAR_TEXTURE_H,
                        0, 0,
                        slotX, baseY,
                        1, SLOT_HEIGHT);

                // First slot body (SLOT_WIDTH-1 px)
                guiGraphics.blitSprite(HOTBAR_LOCATION,
                        HOTBAR_TEXTURE_W, HOTBAR_TEXTURE_H,
                        HOTBAR_FIRST_SLOT_BODY_SRC_X, 0,
                        slotX + 1, baseY,
                        SLOT_WIDTH - 1, SLOT_HEIGHT);
            } else if (i < renderSize - 1) {
                // Middle slots
                guiGraphics.blitSprite(HOTBAR_LOCATION,
                        HOTBAR_TEXTURE_W, HOTBAR_TEXTURE_H,
                        HOTBAR_MID_SLOT_SRC_X, 0,
                        slotX, baseY,
                        SLOT_WIDTH, SLOT_HEIGHT);
            } else {
                // Last slot
                guiGraphics.blitSprite(HOTBAR_LOCATION,
                        HOTBAR_TEXTURE_W, HOTBAR_TEXTURE_H,
                        HOTBAR_LAST_SLOT_SRC_X, 0,
                        slotX, baseY,
                        SLOT_WIDTH + 2, SLOT_HEIGHT);
            }
        }


        int currentIndex;
        if (ClientConfig.EMPTY_SLOT_BEHAVIOR.get() != ClientConfig.EmptySlotBehavior.SKIP) { // COLLAPSE OR CYCLE
            currentIndex = player.getData(OffhandRegistry.OFFHAND_POSITION).getPosition();
        } else { //SKIP
            currentIndex = OffhandInventory.getRenderPosition(player);
        }

        // Draw the selection indicator
        int selectionX = hotbarX + currentIndex * SLOT_WIDTH;
        guiGraphics.blitSprite(SELECTION_LOCATION, selectionX-1, baseY-1, 24, 23);
        guiGraphics.blitSprite(SELECTION_LOCATION, 24, 24, 0,  0 ,selectionX - 1,baseY+ 22, 24, 1);

        RenderSystem.disableBlend();

        // Draw the items
        for (int i = 0; i < items.size(); i++) {
            ItemStack stack = items.get(i);
            int itemPosition = (i + currentIndex) % items.size();
            int itemX = hotbarX + itemPosition * SLOT_WIDTH + 3;
            int itemY = baseY + 3;

            renderItem(guiGraphics, itemX, itemY, deltaTracker, player, stack, true, false);
        }


    }

    @Override
    public void renderDefaultStyleOffhand(GuiGraphics guiGraphics, DeltaTracker deltaTracker, LocalPlayer player, int screenWidth, int screenHeight,
                                                  ItemStack prevItem, ItemStack currentItem, ItemStack nextItem) {

        // Position anchor for Y-coordinate
        int baseY = screenHeight - ITEM_SIZE - 3 + ClientConfig.Y_OFFSET.get();

        // Determine which side to render on based on main arm
        int middleX = getMiddleX(player, screenWidth);

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

        guiGraphics.blitSprite(OFFHAND_LOCATION, prevX + 1, baseY - 3, 29, 24);
        guiGraphics.blitSprite(OFFHAND_LOCATION, nextX, baseY - 3, 29, 24);

        guiGraphics.pose().popPose();

        // Draw the main offhand slot
        guiGraphics.pose().pushPose();

        guiGraphics.blitSprite(OFFHAND_LOCATION, currentX - 3, baseY - 4,29, 24);

        guiGraphics.pose().popPose();

        RenderSystem.disableBlend();


        renderItem(guiGraphics, currentX, baseY, deltaTracker, player, currentItem, true, true);

        RenderSystem.enableBlend();
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 0.5F);

        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(prevX, baseY, 0.0F);
        guiGraphics.pose().scale(SCALE, SCALE, SCALE);
        guiGraphics.pose().translate(-prevX, -baseY, 0.0F);
        renderItem(guiGraphics, prevX+4, baseY+1, deltaTracker, player, prevItem, false, true);
        guiGraphics.pose().popPose();
        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(nextX, baseY, 0.0F);
        guiGraphics.pose().scale(SCALE, SCALE, SCALE);
        guiGraphics.pose().translate(-nextX, -baseY, 0.0F);
        renderItem(guiGraphics, nextX-2, baseY+1, deltaTracker, player, nextItem, false, true);
        guiGraphics.pose().popPose();
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.disableBlend();
    }

    @Override
    public void renderDetailedStyleOffhand(GuiGraphics guiGraphics, DeltaTracker deltaTracker, LocalPlayer player, int screenWidth, int screenHeight,
                                                   ItemStack prevItem, ItemStack currentItem, ItemStack nextItem) {
        // Position anchor for Y-coordinate
        int baseY = screenHeight - ITEM_SIZE - 3 + ClientConfig.Y_OFFSET.get();

        int middleX = getMiddleX(player, screenWidth) - 1;

        // Calculate positions for all three items
        int prevX = middleX - TOTAL_ITEM_SPACE + ClientConfig.X_OFFSET.get();
        int currentX = middleX + ClientConfig.X_OFFSET.get();
        int nextX = middleX + TOTAL_ITEM_SPACE + ClientConfig.X_OFFSET.get();

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableDepthTest();
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

        // Draw the side offhand slots
        guiGraphics.blitSprite(OFFHAND_LOCATION, prevX - 1, baseY- 4, 29, 24);
        guiGraphics.blitSprite(OFFHAND_LOCATION, nextX - 3, baseY -4, 29, 24);

        // Draw the main offhand slot background
        guiGraphics.blitSprite(OFFHAND_LOCATION, 29, 24, 3, 4, currentX + 1, baseY, 16, 16);

        // Draw the selection indicator
        guiGraphics.blitSprite(SELECTION_LOCATION, currentX - 3, baseY - 4,24, 23);

        // Fix for the selection indicator being cut off
        guiGraphics.blitSprite(SELECTION_LOCATION, 24, 24, 0,  0 ,currentX - 3,baseY+ 19, 24, 1);




        renderItem(guiGraphics, currentX + 1, baseY, deltaTracker, player, currentItem, true, true);
        renderItem(guiGraphics, prevX + 2, baseY, deltaTracker, player, prevItem, true, true);
        renderItem(guiGraphics, nextX, baseY, deltaTracker, player, nextItem, true, true);

        RenderSystem.disableBlend();



    }

    @Override
    public int getMiddleX(LocalPlayer player, int screenWidth) {
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
        return middleX;
    }

    @Override
    public void renderItem(GuiGraphics guiGraphics, int x, int y, DeltaTracker deltaTracker, Player player, ItemStack stack, boolean doDecoration, boolean doBounce) {
        if (!stack.isEmpty()) {
            float f = (float)stack.getPopTime() - deltaTracker.getGameTimeDeltaPartialTick(false);
            if (f > 0.0F && doBounce) {
                float f1 = 1.0F + f / 5.0F;
                guiGraphics.pose().pushPose();
                guiGraphics.pose().translate((float)(x + 8), (float)(y + 12), 0.0F);
                guiGraphics.pose().scale(1.0F / f1, (f1 + 1.0F) / 2.0F, 1.0F);
                guiGraphics.pose().translate((float)(-(x + 8)), (float)(-(y + 12)), 0.0F);
            }

            guiGraphics.renderItem(player, stack, x, y, 0);
            if (f > 0.0F && doBounce) {
                guiGraphics.pose().popPose();
            }

            if (doDecoration) {
                guiGraphics.renderItemDecorations(Minecraft.getInstance().font, stack, x, y);
            }
        }
    }

}