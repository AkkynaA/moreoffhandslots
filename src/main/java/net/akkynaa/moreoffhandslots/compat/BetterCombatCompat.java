package net.akkynaa.moreoffhandslots.compat;

import com.mojang.logging.LogUtils;
import net.minecraft.world.entity.player.Player;
import net.neoforged.fml.ModList;
import org.slf4j.Logger;

/**
 * Compatibility layer for interactions with Better Combat mod
 */
public class BetterCombatCompat {
    static final Logger LOGGER = LogUtils.getLogger();
    private static final String BETTER_COMBAT_MODID = "bettercombat";
    private static Boolean isBetterCombatLoaded = null;

    // Cached reflection references, initialised on first use
    private static java.lang.reflect.Method cachedGetAttributesMethod = null;
    private static java.lang.reflect.Method cachedIsTwoHandedMethod = null;

    /**
     * Checks if Better Combat mod is loaded
     */
    public static boolean isBetterCombatLoaded() {
        if (isBetterCombatLoaded == null) {
            isBetterCombatLoaded = ModList.get().isLoaded(BETTER_COMBAT_MODID);
            if (isBetterCombatLoaded) {
                LOGGER.info("Better Combat mod detected. Compatibility features will be enabled.");
            } else {
                LOGGER.info("Better Combat mod not detected. Compatibility features will be disabled.");
            }
        }
        return isBetterCombatLoaded;
    }

    /**
     * Checks if the player has a two-handed weapon equipped
     *
     * @param player The player to check
     * @return true if a two-handed weapon is equipped
     */
    public static boolean hasTwoHandedWeaponEquipped(Player player) {
        if (!isBetterCombatLoaded()) {
            return false;
        }

        try {
            if (cachedGetAttributesMethod == null) {
                Class<?> weaponRegistryClass = Class.forName("net.bettercombat.logic.WeaponRegistry");
                cachedGetAttributesMethod = weaponRegistryClass.getMethod("getAttributes", net.minecraft.world.item.ItemStack.class);
            }

            // Check main hand item
            Object mainHandAttributes = cachedGetAttributesMethod.invoke(null, player.getMainHandItem());
            if (mainHandAttributes != null) {
                if (cachedIsTwoHandedMethod == null) {
                    cachedIsTwoHandedMethod = mainHandAttributes.getClass().getMethod("isTwoHanded");
                }
                if ((boolean) cachedIsTwoHandedMethod.invoke(mainHandAttributes)) {
                    return true;
                }
            }

            // Check offhand item
            Object offHandAttributes = cachedGetAttributesMethod.invoke(null, player.getOffhandItem());
            if (offHandAttributes != null) {
                if (cachedIsTwoHandedMethod == null) {
                    cachedIsTwoHandedMethod = offHandAttributes.getClass().getMethod("isTwoHanded");
                }
                if ((boolean) cachedIsTwoHandedMethod.invoke(offHandAttributes)) {
                    return true;
                }
            }

            return false;
        } catch (Exception e) {
            LOGGER.error("Failed to check for two-handed weapons: ", e);
            return false;
        }
    }
}