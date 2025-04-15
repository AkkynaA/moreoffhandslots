package akkynaa.moreoffhandslots.mixin;

import akkynaa.moreoffhandslots.api.OffhandInventory;
import akkynaa.moreoffhandslots.client.config.ClientConfig;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.AttackIndicatorStatus;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static net.minecraft.client.gui.Gui.GUI_ICONS_LOCATION;
import static net.minecraft.client.gui.components.AbstractWidget.WIDGETS_LOCATION;


@Mixin(value = Gui.class, priority = 10000)
public class GuiMixin {

    @Inject(
            method = "renderHotbar(FLnet/minecraft/client/gui/GuiGraphics;)V",
            at = @At("HEAD"),
            cancellable = true
    )
    public void renderHotbar(float p_283031_, GuiGraphics p_282108_, CallbackInfo ci) {
        ci.cancel();
        Player player = ((Gui) (Object) this).getCameraPlayer();
        if (player != null) {
            ItemStack itemstack = player.getOffhandItem();
            HumanoidArm humanoidarm = player.getMainArm().getOpposite();
            int i = ((Gui) (Object) this).screenWidth / 2;
            if (ClientConfig.ALIGN_TO_CENTER.get() && ClientConfig.INDICATOR_STYLE.get() == ClientConfig.IndicatorStyle.HOTBAR) {
                i = OffhandInventory.getHotbarOffset();
                if (!ClientConfig.RENDER_EMPTY_OFFHAND.get() && OffhandInventory.getOffhandItemsToRender(player).get(0).isEmpty()){
                    i = ((Gui) (Object) this).screenWidth / 2;
                }
            }
            int j = 182;
            int k = 91;
            p_282108_.pose().pushPose();
            p_282108_.pose().translate(0.0F, 0.0F, -90.0F);
            p_282108_.blit(WIDGETS_LOCATION, i - 91, ((Gui) (Object) this).screenHeight - 22, 0, 0, 182, 22);
            p_282108_.blit(WIDGETS_LOCATION, i - 91 - 1 + player.getInventory().selected * 20, ((Gui) (Object) this).screenHeight - 22 - 1, 0, 22, 24, 22);
            if (!itemstack.isEmpty() && ClientConfig.INDICATOR_STYLE.get() == ClientConfig.IndicatorStyle.VANILLA) {
                if (humanoidarm == HumanoidArm.LEFT) {
                    p_282108_.blit(WIDGETS_LOCATION, i - 91 - 29, ((Gui) (Object) this).screenHeight - 23, 24, 22, 29, 24);
                } else {
                    p_282108_.blit(WIDGETS_LOCATION, i + 91, ((Gui) (Object) this).screenHeight - 23, 53, 22, 29, 24);
                }
            }

            p_282108_.pose().popPose();
            int l = 1;

            for(int i1 = 0; i1 < 9; ++i1) {
                int j1 = i - 90 + i1 * 20 + 2;
                int k1 = ((Gui) (Object) this).screenHeight - 16 - 3;
                ((Gui) (Object) this).renderSlot(p_282108_, j1, k1, p_283031_, player, player.getInventory().items.get(i1), l++);
            }

            if (!itemstack.isEmpty() && ClientConfig.INDICATOR_STYLE.get() == ClientConfig.IndicatorStyle.VANILLA) {
                int i2 = ((Gui) (Object) this).screenHeight - 16 - 3;
                if (humanoidarm == HumanoidArm.LEFT) {
                    ((Gui) (Object) this).renderSlot(p_282108_, i - 91 - 26, i2, p_283031_, player, itemstack, l++);
                } else {
                    ((Gui) (Object) this).renderSlot(p_282108_, i + 91 + 10, i2, p_283031_, player, itemstack, l++);
                }
            }

            RenderSystem.enableBlend();
            if (((Gui) (Object) this).minecraft.options.attackIndicator().get() == AttackIndicatorStatus.HOTBAR) {
                float f = ((Gui) (Object) this).minecraft.player.getAttackStrengthScale(0.0F);
                if (f < 1.0F) {
                    int j2 = ((Gui) (Object) this).screenHeight - 20;
                    int k2 = i + 91 + 6;
                    if (humanoidarm == HumanoidArm.RIGHT) {
                        k2 = i - 91 - 22;
                    }

                    int l1 = (int)(f * 19.0F);
                    p_282108_.blit(GUI_ICONS_LOCATION, k2, j2, 0, 94, 18, 18);
                    p_282108_.blit(GUI_ICONS_LOCATION, k2, j2 + 18 - l1, 18, 112 - l1, 18, l1);
                }
            }

            RenderSystem.disableBlend();
        }
    }
}
