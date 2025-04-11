package akkynaa.moreoffhandslots.capability;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;

public class ModCapabilities {
    public static final Capability<OffhandPosition> OFFHAND_POSITION = CapabilityManager.get(new CapabilityToken<>() {});
}
