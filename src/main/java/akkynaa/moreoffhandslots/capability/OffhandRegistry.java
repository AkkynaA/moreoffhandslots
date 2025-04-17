package akkynaa.moreoffhandslots.capability;


import akkynaa.moreoffhandslots.MoreOffhandSlots;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import java.util.function.Supplier;

public class OffhandRegistry {

    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES =
            DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, MoreOffhandSlots.MODID);

    public static final Supplier<AttachmentType<OffhandPosition>> OFFHAND_POSITION =
            ATTACHMENT_TYPES.register("offhand_position", () ->
                    AttachmentType.serializable(OffhandPosition::new).copyOnDeath().build()
            );

    public static void init(IEventBus modEventBus) {
        ATTACHMENT_TYPES.register(modEventBus);
    }
}
