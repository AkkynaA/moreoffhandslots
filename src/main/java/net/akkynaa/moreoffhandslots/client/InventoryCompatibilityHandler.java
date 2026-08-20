package net.akkynaa.moreoffhandslots.client;

import net.akkynaa.moreoffhandslots.MoreOffhandSlots;
import net.akkynaa.moreoffhandslots.client.config.ClientConfig;
import net.akkynaa.slotlib.client.gui.SlotLibButton;
import net.akkynaa.slotlib.client.gui.SlotLibScreen;
import net.akkynaa.slotlib.common.network.client.CPacketOpenSlotLib;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ScreenEvent;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.ArrayList;
import java.util.List;

@EventBusSubscriber(modid = MoreOffhandSlots.MODID, bus = EventBusSubscriber.Bus.GAME, value = Dist.CLIENT)
public class InventoryCompatibilityHandler {

    private static final List<GuiEventListener> PRESERVED_MOD_WIDGETS = new ArrayList<>();

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onScreenInit(ScreenEvent.Init.Post event) {
        if (!ClientConfig.HIDE_SLOT_BUTTON.get()) return;

        removeSlotLibButton(event);

        var screen = event.getScreen();
        if (screen instanceof InventoryScreen || screen instanceof CreativeModeInventoryScreen) {
            preserveModWidgets(event);
            openSlotLibScreen();
        } else if (screen instanceof SlotLibScreen) {
            restoreModWidgets(event);
        }
    }

    private static void removeSlotLibButton(ScreenEvent.Init.Post event) {
        for (GuiEventListener listener : List.copyOf(event.getListenersList())) {
            if (listener instanceof SlotLibButton) {
                event.removeListener(listener);
            }
        }
    }

    private static void preserveModWidgets(ScreenEvent.Init.Post event) {
        PRESERVED_MOD_WIDGETS.clear();

        for (GuiEventListener listener : event.getListenersList()) {
            String packageName = listener.getClass().getPackageName();
            if (!packageName.startsWith("net.minecraft.") && !packageName.startsWith("net.akkynaa.slotlib.")) {
                PRESERVED_MOD_WIDGETS.add(listener);
            }
        }
    }

    private static void restoreModWidgets(ScreenEvent.Init.Post event) {
        for (GuiEventListener widget : PRESERVED_MOD_WIDGETS) {
            boolean alreadyPresent = event.getListenersList().stream()
                    .anyMatch(listener -> listener.getClass() == widget.getClass());
            if (!alreadyPresent) {
                event.addListener(widget);
            }
        }
        PRESERVED_MOD_WIDGETS.clear();
    }

    private static void openSlotLibScreen() {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player == null) return;

        ItemStack carriedItem = minecraft.player.containerMenu.getCarried();
        minecraft.player.containerMenu.setCarried(ItemStack.EMPTY);
        PacketDistributor.sendToServer(new CPacketOpenSlotLib(carriedItem));
    }
}
