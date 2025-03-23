package akkynaa.moreoffhandslots.mixin;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.loading.FMLLoader;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import top.theillusivec4.curios.client.ClientEventHandler;
import java.util.ArrayList;
import java.util.List;


@Mixin(ClientEventHandler.class)
public class CuriosClientEventHandlerMixin {
    @ModifyVariable(
            method = "onTooltip",
            at = @At(
                    value = "STORE"
            ),
            name = "slots",
            remap = false
    )
    private List<String> removeOffhandTag(List<String> originalSlots) {
        if (FMLLoader.getDist() == Dist.CLIENT) {
            List<String> slots = new ArrayList<>(originalSlots);
            slots.remove("offhand");
            return slots;
        }
        return originalSlots;
    }

}
