package akkynaa.moreoffhandslots.capability;

import akkynaa.moreoffhandslots.MoreOffhandSlots;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;

@Mod.EventBusSubscriber
public class OffhandPositionManager {

    @SubscribeEvent
    public static  void attachCapabilityToEntityHandler(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof Player) {
            OffhandPosition position = new OffhandPosition();
            LazyOptional<OffhandPosition> capOptional = LazyOptional.of(() -> position);
            Capability<OffhandPosition> capability = ModCapabilities.OFFHAND_POSITION;

            ICapabilityProvider provider = new ICapabilitySerializable<CompoundTag>() {

                @Nonnull
                @Override
                public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
                    if (cap == capability) {
                        return capOptional.cast();
                    }
                    return LazyOptional.empty();
                }

                @Override
                public CompoundTag serializeNBT() {
                    return position.serializeNBT();
                }

                @Override
                public void deserializeNBT(CompoundTag nbt) {
                    position.deserializeNBT(nbt);
                }
            };
            event.addCapability(ResourceLocation.fromNamespaceAndPath(MoreOffhandSlots.MODID, "offhand_position"), provider);
        }
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase == TickEvent.Phase.START && event.player instanceof ServerPlayer serverPlayer) {
            serverPlayer.getCapability(ModCapabilities.OFFHAND_POSITION).ifPresent(offhandPosition -> {
                offhandPosition.updateData(serverPlayer);
            });
        }
    }

    @SubscribeEvent
    public static void onPlayerCloned(PlayerEvent.Clone event) {
        if (event.isWasDeath()) {
            event.getOriginal().getCapability(ModCapabilities.OFFHAND_POSITION).ifPresent(oldStore ->
                    event.getEntity().getCapability(ModCapabilities.OFFHAND_POSITION).ifPresent(newStore -> {
                        newStore.copyFrom(oldStore);
                    }));
        }
    }
}
