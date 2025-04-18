package akkynaa.moreoffhandslots.api;

import akkynaa.moreoffhandslots.client.render.OffhandHudRenderer;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.List;

/**
 * The offhand HUD renderer implementation exposed as an interface for better compatibility with other mods.
 */
public interface IOffhandHudRenderer {


    /**
     * Renders the offhand HUD.
     */
    void renderOffhandHud(GuiGraphics guiGraphics, float partialTicks, int screenWidth, int screenHeight);

    /**
     * Renders the hotbar style offhand HUD.
     */
    void renderHotbarStyleOffhand(GuiGraphics guiGraphics, float partialTicks, LocalPlayer player,
                                  int screenWidth, int screenHeight, List<ItemStack> items);

    /**
     * Renders the default style offhand HUD.
     */
    void renderDefaultStyleOffhand(GuiGraphics guiGraphics, float partialTicks, LocalPlayer player, int screenWidth, int screenHeight,
                                   ItemStack prevItem, ItemStack currentItem, ItemStack nextItem);

    /**
     * Renders the detailed style offhand HUD.
     */
    void renderDetailedStyleOffhand(GuiGraphics guiGraphics, float partialTicks, LocalPlayer player, int screenWidth, int screenHeight,
                                    ItemStack prevItem, ItemStack currentItem, ItemStack nextItem);

    /**
     * Get the middle X coordinate for the offhand HUD.
     */
    int getMiddleX(LocalPlayer player, int screenWidth);

    /**
     * Renders an item in the offhand HUD, similar to how vanilla does it.
     */
    void renderItem(GuiGraphics guiGraphics, int x, int y, float partialTicks, Player player, ItemStack stack, boolean doDecoration, boolean doBounce);

    /**
     * Set the currently used offhand HUD renderer.
     */
    static void setOffhandHudRenderer(IOffhandHudRenderer renderer) {
        OffhandHudRenderer.setOffhandRenderer(renderer);
    }

    /**
     * Get the currently used offhand HUD renderer.
     */
    static IOffhandHudRenderer getOffhandHudRenderer() {
        return OffhandHudRenderer.getOffhandRenderer();
    }
}
