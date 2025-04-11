package akkynaa.moreoffhandslots.mixin;

import akkynaa.moreoffhandslots.api.OffhandInventory;
import akkynaa.moreoffhandslots.client.config.ClientConfig;
import net.minecraft.client.gui.Gui;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;


@Mixin(Gui.class)
public class GuiMixin {

    @Redirect(
            method = "renderHotbar(FLnet/minecraft/client/gui/GuiGraphics;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/player/Player;getOffhandItem()Lnet/minecraft/world/item/ItemStack;"
            )
    )
    private ItemStack hideOffhandItem(Player player) {
        if (ClientConfig.INDICATOR_STYLE.get() != ClientConfig.IndicatorStyle.VANILLA) {
            return ItemStack.EMPTY;
        }
        return player.getOffhandItem();
    }

    @ModifyVariable(
            method = "renderHotbar",
            at = @At(
                    value = "STORE"

            ),
            name = "i",
            remap = false
    )
    private int modifyScreenCenter(int i) {
        // Adjust the screen center based on the current indicator style
        if (ClientConfig.ALIGN_TO_CENTER.get() && ClientConfig.INDICATOR_STYLE.get() == ClientConfig.IndicatorStyle.HOTBAR) {
            return OffhandInventory.getHotbarOffset();
        }
        return i;
    }

}
