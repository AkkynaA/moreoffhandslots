package akkynaa.moreoffhandslots;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.neoforged.neoforge.client.settings.KeyConflictContext;
import org.lwjgl.glfw.GLFW;


public class KeyBindings {
    public static KeyMapping NEXT_OFFHAND_KEY = new KeyMapping(
            "key." + MoreOffhandSlots.MODID + ".next_offhand",
            KeyConflictContext.IN_GAME,
            InputConstants.Type.MOUSE,
            GLFW.GLFW_MOUSE_BUTTON_5,
            "key.categories." + MoreOffhandSlots.MODID
    );

    public static KeyMapping PREV_OFFHAND_KEY =  new KeyMapping(
            "key." + MoreOffhandSlots.MODID + ".prev_offhand",
            KeyConflictContext.IN_GAME,
            InputConstants.Type.MOUSE,
            GLFW.GLFW_MOUSE_BUTTON_4,
            "key.categories." + MoreOffhandSlots.MODID
    );
}