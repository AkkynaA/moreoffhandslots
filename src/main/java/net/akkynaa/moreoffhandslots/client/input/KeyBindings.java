package net.akkynaa.moreoffhandslots.client.input;

import com.mojang.blaze3d.platform.InputConstants;
import net.akkynaa.moreoffhandslots.MoreOffhandSlots;
import net.minecraft.client.KeyMapping;
import net.neoforged.neoforge.client.settings.KeyConflictContext;
import org.lwjgl.glfw.GLFW;


public class KeyBindings {

    public static final String CATEGORY = "key.categories." + MoreOffhandSlots.MODID;

    public static KeyMapping NEXT_OFFHAND_KEY = new KeyMapping(
            "key." + MoreOffhandSlots.MODID + ".next_offhand",
            KeyConflictContext.IN_GAME,
            InputConstants.Type.MOUSE,
            GLFW.GLFW_MOUSE_BUTTON_5,
            CATEGORY
    );

    public static KeyMapping PREV_OFFHAND_KEY =  new KeyMapping(
            "key." + MoreOffhandSlots.MODID + ".prev_offhand",
            KeyConflictContext.IN_GAME,
            InputConstants.Type.MOUSE,
            GLFW.GLFW_MOUSE_BUTTON_4,
            CATEGORY
    );

    public static KeyMapping SCROLLWHEEL_MODIFIER = new KeyMapping(
            "key." + MoreOffhandSlots.MODID + ".scrollwheel_modifier",
            KeyConflictContext.IN_GAME,
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_LEFT_SHIFT,
            CATEGORY
    );
}