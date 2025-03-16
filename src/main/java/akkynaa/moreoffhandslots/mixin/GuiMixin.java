package akkynaa.moreoffhandslots.mixin;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * This mixin removes the vanilla offhand display by intercepting the call to get
 * the player's offhand item and returning an empty ItemStack.
 */
@Mixin(Gui.class)
public class GuiMixin {

    /**
     * This mixin manipulates the renderHotbar method to hide the offhand item.
     * It intercepts the call to Player.getOffhandItem() and returns an empty stack
     * instead of the actual offhand item, which effectively disables the rendering
     * of the offhand item in the HUD.
     */
    @Redirect(
            method = "renderHotbar(FLnet/minecraft/client/gui/GuiGraphics;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/player/Player;getOffhandItem()Lnet/minecraft/world/item/ItemStack;"
            )
    )
    private ItemStack hideOffhandItem(Player player) {
        // Return an empty ItemStack instead of the player's actual offhand item
        return ItemStack.EMPTY;
    }
}