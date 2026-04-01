//
//package net.akkynaa.moreoffhandslots.compat;
//
//import mod.crend.autohud.AutoHud;
//import mod.crend.autohud.api.AutoHudApi;
//import mod.crend.autohud.component.Component;
//import mod.crend.autohud.component.state.ItemStackComponentState;
//import mod.crend.autohud.render.AutoHudRenderer;
//import mod.crend.autohud.render.ComponentRenderer;
//import net.akkynaa.moreoffhandslots.api.IOffhandHudRenderer;
//import net.akkynaa.moreoffhandslots.client.render.OffhandHudRenderer;
//import net.minecraft.client.DeltaTracker;
//import net.minecraft.client.gui.GuiGraphics;
//import net.minecraft.client.player.LocalPlayer;
//import net.minecraft.world.entity.player.Player;
//import net.minecraft.world.item.ItemStack;
//
//import java.util.List;
//import java.util.Objects;
//
//import static mod.crend.autohud.component.Components.Hotbar;
//
//
//public class AutoHudCompat implements AutoHudApi {
//    public static Component OFFHAND_RENDERER_COMPONENT;
//    public static ComponentRenderer COMPONENT_WRAPPER;
//    public static ComponentRenderer BACKGROUND_WRAPPER;
//    public static ComponentRenderer ITEM_WRAPPER;
//
//    public static void register() {
//        AutoHud.addApi(new AutoHudCompat());
//    }
//
//    public AutoHudCompat() {
//    }
//
//    public String modId() {
//        return "net/akkynaa/moreoffhandslots";
//    }
//
//    public void init() {
//        IOffhandHudRenderer.setOffhandHudRenderer(new WrappedOffhandHudRenderer((OffhandHudRenderer) IOffhandHudRenderer.getOffhandHudRenderer()));
//    }
//
//    public void initState(LocalPlayer player) {
//        OFFHAND_RENDERER_COMPONENT.reveal();
//    }
//
//    public void tickState(LocalPlayer player) {
//        if (OFFHAND_RENDERER_COMPONENT.fullyRevealed()) {
//            OFFHAND_RENDERER_COMPONENT.synchronizeFrom(new Component[]{Hotbar});
//        }
//
//    }
//
//    static {
//        OFFHAND_RENDERER_COMPONENT = Component.builder("net/akkynaa/moreoffhandslots")
//                .isTargeted(() -> AutoHud.targetHotbar)
//                .config(AutoHud.config.hotbar())
//                .inMainHud()
//                .state((player) -> new ItemStackComponentState(
//                        OFFHAND_RENDERER_COMPONENT,
//                        player::getOffhandItem, true)
//                )
//                .build();
//
//        COMPONENT_WRAPPER = ComponentRenderer.of(OFFHAND_RENDERER_COMPONENT);
//
//        ComponentRenderer.Builder builder = ComponentRenderer.builder(OFFHAND_RENDERER_COMPONENT)
//                .fade()
//                .isActive(() -> OFFHAND_RENDERER_COMPONENT.isActive() && AutoHud.config.animationFade())
//                .doRender(AutoHudRenderer::shouldRenderHotbarItems)
//                .withCustomFramebuffer(true);
//
//        ComponentRenderer componentWrapper = COMPONENT_WRAPPER;
//
//        Objects.requireNonNull(componentWrapper);
//
//        BACKGROUND_WRAPPER = builder.beginRender(componentWrapper::endFade).build();
//
//        builder = ComponentRenderer.builder(OFFHAND_RENDERER_COMPONENT)
//                .fade()
//                .isActive(() -> OFFHAND_RENDERER_COMPONENT.isActive() && AutoHud.config.animationFade())
//                .doRender(() -> !OFFHAND_RENDERER_COMPONENT
//                        .fullyHidden()
//                        || AutoHud.config.animationFade()
//                        && AutoHud.config.getHotbarItemsMaximumFade() > 0.0F
//                        || !AutoHud.config.animationFade()
//                        && AutoHud.config.animationMove()
//                )
//                .withCustomFramebuffer(true);
//
//        componentWrapper = COMPONENT_WRAPPER;
//
//        Objects.requireNonNull(componentWrapper);
//
//        ITEM_WRAPPER = builder.beginRender(componentWrapper::endFade).build();
//    }
//
//    static class WrappedOffhandHudRenderer implements IOffhandHudRenderer {
//        OffhandHudRenderer parent;
//
//        public WrappedOffhandHudRenderer(OffhandHudRenderer parent) {
//            this.parent = parent;
//        }
//
//
//        @Override
//        public void renderOffhandHud(GuiGraphics guiGraphics, DeltaTracker deltaTracker) {
//            AutoHudCompat.COMPONENT_WRAPPER.wrap(guiGraphics, () -> this.parent.renderOffhandHud(guiGraphics, deltaTracker));
//        }
//
//        @Override
//        public void renderHotbarStyleOffhand(GuiGraphics guiGraphics, DeltaTracker deltaTracker, LocalPlayer player, int screenWidth, int screenHeight, List<ItemStack> items) {
//            AutoHudCompat.BACKGROUND_WRAPPER.wrap(guiGraphics, () -> this.parent.renderHotbarStyleOffhand(guiGraphics, deltaTracker, player, screenWidth, screenHeight, items));
//        }
//
//        @Override
//        public void renderDefaultStyleOffhand(GuiGraphics guiGraphics, DeltaTracker deltaTracker, LocalPlayer player, int screenWidth, int screenHeight, ItemStack prevItem, ItemStack currentItem, ItemStack nextItem) {
//            AutoHudCompat.BACKGROUND_WRAPPER.wrap(guiGraphics, () -> this.parent.renderDefaultStyleOffhand(guiGraphics, deltaTracker, player, screenWidth, screenHeight, prevItem, currentItem, nextItem));
//        }
//
//        @Override
//        public void renderDetailedStyleOffhand(GuiGraphics guiGraphics, DeltaTracker deltaTracker, LocalPlayer player, int screenWidth, int screenHeight, ItemStack prevItem, ItemStack currentItem, ItemStack nextItem) {
//            AutoHudCompat.BACKGROUND_WRAPPER.wrap(guiGraphics, () -> this.parent.renderDetailedStyleOffhand(guiGraphics, deltaTracker, player, screenWidth, screenHeight, prevItem, currentItem, nextItem));
//        }
//
//        @Override
//        public int getMiddleX(LocalPlayer player, int screenWidth) {
//            return this.parent.getMiddleX(player, screenWidth);
//        }
//
//        @Override
//        public void renderItem(GuiGraphics guiGraphics, int x, int y, DeltaTracker deltaTracker, Player player, ItemStack stack, boolean doDecoration, boolean doBounce) {
//            AutoHudCompat.ITEM_WRAPPER.wrap(guiGraphics, () -> this.parent.renderItem(guiGraphics, x, y, deltaTracker, player, stack, doDecoration, doBounce));
//        }
//    }
//}
