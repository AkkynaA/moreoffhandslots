package net.akkynaa.moreoffhandslots.mixin;


import net.akkynaa.moreoffhandslots.client.config.ClientConfig;
import net.akkynaa.moreoffhandslots.client.render.OffhandHudRenderer;
import com.simibubi.create.content.equipment.toolbox.ToolboxHandlerClient;
import net.neoforged.fml.ModList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(ToolboxHandlerClient.class)
public class CreateMixin {

    @ModifyVariable(
            method = "renderOverlay",
            at = @At(
                    value = "STORE"
            ),
            name = "x",
            remap = false,
            require = 0
    )
    private static int modifyToolbarOffset(int value) {
        if (ModList.get().isLoaded("create")
                && ClientConfig.ALIGN_TO_CENTER.get()
                && ClientConfig.INDICATOR_STYLE.get() == ClientConfig.IndicatorStyle.HOTBAR) {
            value = OffhandHudRenderer.getHotbarOffset() - 90;
        }
        return value;
    }


}
