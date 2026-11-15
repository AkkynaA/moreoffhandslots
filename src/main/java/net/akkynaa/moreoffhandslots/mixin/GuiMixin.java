package net.akkynaa.moreoffhandslots.mixin;

import net.akkynaa.moreoffhandslots.api.OffhandInventory;
import net.akkynaa.moreoffhandslots.client.config.ClientConfig;
import net.akkynaa.moreoffhandslots.client.render.OffhandHudRenderer;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(Gui.class)
public class GuiMixin {

    /**
     * Moves the main hotbar when centered hotbars are enabled.
     */
    @ModifyVariable(method = "renderItemHotbar", at = @At("STORE"), ordinal = 0)
    private int moreoffhandslots$centerHotbar(int centerX, GuiGraphics guiGraphics, DeltaTracker deltaTracker) {
        if (!ClientConfig.ALIGN_TO_CENTER.get() || ClientConfig.INDICATOR_STYLE.get() != ClientConfig.IndicatorStyle.HOTBAR) {
            return centerX;
        }
        Player player = ((Gui) (Object) this).getCameraPlayer();
        if (player != null && !ClientConfig.RENDER_EMPTY_OFFHAND.get()
                && OffhandInventory.getOffhandItemsToRender(player).getFirst().isEmpty()) {
            return centerX;
        }
        return OffhandHudRenderer.getHotbarOffset();
    }

    /**
     * Hides the vanilla offhand slot unless the VANILLA indicator style is selected;
     * the other styles draw their own offhand HUD.
     */
    @ModifyVariable(method = "renderItemHotbar", at = @At("STORE"), ordinal = 0)
    private ItemStack moreoffhandslots$hideVanillaOffhand(ItemStack offhandItem) {
        return ClientConfig.INDICATOR_STYLE.get() == ClientConfig.IndicatorStyle.VANILLA ? offhandItem : ItemStack.EMPTY;
    }
}
