package akkynaa.moreoffhandslots.mixin;

import com.anthonyhilyard.equipmentcompare.compat.CuriosHandler;
import net.minecraftforge.fml.ModList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.Set;

@Mixin(CuriosHandler.class)
public class EquipmentCompareMixin {

    @ModifyVariable(
            method = "getCuriosMatchingSlot",
            at = @At(value="STORE"),
            name = "tags",
            remap = false,
            require = 0
    )
    private static Set<String> removeOffhandTags(Set<String> tags) {
        if (ModList.get().isLoaded("equipmentcompare")) {
            tags.remove("offhand");
            return tags;
        }
        return tags;
    }
}
