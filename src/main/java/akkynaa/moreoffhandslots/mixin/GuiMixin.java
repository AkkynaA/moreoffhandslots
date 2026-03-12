package akkynaa.moreoffhandslots.mixin;

import akkynaa.moreoffhandslots.api.OffhandInventory;
import akkynaa.moreoffhandslots.client.config.ClientConfig;
import akkynaa.moreoffhandslots.client.render.OffhandHudRenderer;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.AttackIndicatorStatus;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static net.minecraft.client.gui.Gui.*;

@Mixin(Gui.class)
public class GuiMixin {

    @Inject(
            method = "Lnet/minecraft/client/gui/Gui;renderItemHotbar(Lnet/minecraft/client/gui/GuiGraphics;Lnet/minecraft/client/DeltaTracker;)V",
            at = @At("HEAD"),
            cancellable = true
    )
    private void renderItemHotbar(GuiGraphics guiGraphics, DeltaTracker deltaTracker, CallbackInfo ci) {
        ci.cancel();
        Player player = ((Gui) (Object) this).getCameraPlayer();
        if (player != null) {
            ItemStack itemstack = player.getOffhandItem();
            HumanoidArm humanoidarm = player.getMainArm().getOpposite();
            int i = guiGraphics.guiWidth() / 2;
            if (ClientConfig.ALIGN_TO_CENTER.get() && ClientConfig.INDICATOR_STYLE.get() == ClientConfig.IndicatorStyle.HOTBAR) {
                i = OffhandHudRenderer.getHotbarOffset();
                if (!ClientConfig.RENDER_EMPTY_OFFHAND.get() && OffhandInventory.getOffhandItemsToRender(player).getFirst().isEmpty()){
                    i = guiGraphics.guiWidth() / 2;
                }
            }
            int j = 182;
            int k = 91;
            RenderSystem.enableBlend();
            guiGraphics.pose().pushPose();
            guiGraphics.pose().translate(0.0F, 0.0F, -90.0F);
            guiGraphics.blitSprite(HOTBAR_SPRITE, i - 91, guiGraphics.guiHeight() - 22, 182, 22);
            guiGraphics.blitSprite(HOTBAR_SELECTION_SPRITE, i - 91 - 1 + player.getInventory().selected * 20, guiGraphics.guiHeight() - 22 - 1, 24, 23);
            if (!itemstack.isEmpty() && ClientConfig.INDICATOR_STYLE.get() == ClientConfig.IndicatorStyle.VANILLA) {
                if (humanoidarm == HumanoidArm.LEFT) {
                    guiGraphics.blitSprite(HOTBAR_OFFHAND_LEFT_SPRITE, i - 91 - 29, guiGraphics.guiHeight() - 23, 29, 24);
                } else {
                    guiGraphics.blitSprite(HOTBAR_OFFHAND_RIGHT_SPRITE, i + 91, guiGraphics.guiHeight() - 23, 29, 24);
                }
            }

            guiGraphics.pose().popPose();
            RenderSystem.disableBlend();
            int l = 1;

            for (int i1 = 0; i1 < 9; i1++) {
                int j1 = i - 90 + i1 * 20 + 2;
                int k1 = guiGraphics.guiHeight() - 16 - 3;
                ((Gui) (Object) this).renderSlot(guiGraphics, j1, k1, deltaTracker, player, player.getInventory().items.get(i1), l++);
            }

            if (!itemstack.isEmpty() && ClientConfig.INDICATOR_STYLE.get() == ClientConfig.IndicatorStyle.VANILLA) {
                int i2 = guiGraphics.guiHeight() - 16 - 3;
                if (humanoidarm == HumanoidArm.LEFT) {
                    ((Gui) (Object) this).renderSlot(guiGraphics, i - 91 - 26, i2, deltaTracker, player, itemstack, l++);
                } else {
                    ((Gui) (Object) this).renderSlot(guiGraphics, i + 91 + 10, i2, deltaTracker, player, itemstack, l++);
                }
            }

            if (((Gui) (Object) this).minecraft.options.attackIndicator().get() == AttackIndicatorStatus.HOTBAR) {
                RenderSystem.enableBlend();
                float f = ((Gui) (Object) this).minecraft.player.getAttackStrengthScale(0.0F);
                if (f < 1.0F) {
                    int j2 = guiGraphics.guiHeight() - 20;
                    int k2 = i + 91 + 6;
                    if (humanoidarm == HumanoidArm.RIGHT) {
                        k2 = i - 91 - 22;
                    }

                    int l1 = (int)(f * 19.0F);
                    guiGraphics.blitSprite(HOTBAR_ATTACK_INDICATOR_BACKGROUND_SPRITE, k2, j2, 18, 18);
                    guiGraphics.blitSprite(HOTBAR_ATTACK_INDICATOR_PROGRESS_SPRITE, 18, 18, 0, 18 - l1, k2, j2 + 18 - l1, 18, l1);
                }

                RenderSystem.disableBlend();
            }
        }
    }
}