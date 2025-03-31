package akkynaa.moreoffhandslots.compat;

import com.mojang.logging.LogUtils;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.fml.ModList;
import org.slf4j.Logger;

/**
 * Compatibility layer for interactions with Better Combat mod
 */
public class BetterCombatCompat {
    static final Logger LOGGER = LogUtils.getLogger();
    private static final String BETTER_COMBAT_MODID = "bettercombat";
    private static Boolean isBetterCombatLoaded = null;

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

        // Use reflection to access Better Combat classes
        try {
            // Get the WeaponRegistry class
            Class<?> weaponRegistryClass = Class.forName("net.bettercombat.logic.WeaponRegistry");

            // Get the getAttributes method
            java.lang.reflect.Method getAttributesMethod = weaponRegistryClass.getMethod("getAttributes", net.minecraft.world.item.ItemStack.class);

            // Check main hand item
            Object mainHandAttributes = getAttributesMethod.invoke(null, player.getMainHandItem());
            if (mainHandAttributes != null) {
                java.lang.reflect.Method isTwoHandedMethod = mainHandAttributes.getClass().getMethod("isTwoHanded");
                boolean mainHandIsTwoHanded = (boolean) isTwoHandedMethod.invoke(mainHandAttributes);
                if (mainHandIsTwoHanded) {
                    return true;
                }
            }

            // Check offhand item
            Object offHandAttributes = getAttributesMethod.invoke(null, player.getOffhandItem());
            if (offHandAttributes != null) {
                java.lang.reflect.Method isTwoHandedMethod = offHandAttributes.getClass().getMethod("isTwoHanded");
                boolean offHandIsTwoHanded = (boolean) isTwoHandedMethod.invoke(offHandAttributes);
                if (offHandIsTwoHanded) {
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