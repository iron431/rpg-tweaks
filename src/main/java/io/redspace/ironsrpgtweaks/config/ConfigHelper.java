package io.redspace.ironsrpgtweaks.config;

import io.redspace.ironsrpgtweaks.durability_module.DeathDurabilityMode;
import io.redspace.ironsrpgtweaks.durability_module.VanillaDurabilityMode;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;

public class ConfigHelper {
    public static class Durability {
        public static boolean shouldTakeVanillaDamage(ItemStack itemStack) {
            if (!ServerConfigs.DURABILITY_MODULE_ENABLED.get()) {
                return true;
            }
            var mode = ServerConfigs.DURABILITY_VANILLA_MODE.get();
            if (mode == VanillaDurabilityMode.NONE) {
                return false;
            }
            if (mode == VanillaDurabilityMode.ALL) {
                return true;
            }
            return itemStack.getItem() instanceof ArmorItem ? mode == VanillaDurabilityMode.ARMOR : mode == VanillaDurabilityMode.TOOLS;
        }

        public static boolean shouldTakeDeathDamage(ItemStack itemStack) {
            if (!ServerConfigs.DURABILITY_MODULE_ENABLED.get()) {
                return false;
            }
            var mode = ServerConfigs.DURABILITY_DEATH_MODE.get();
            if (mode == DeathDurabilityMode.ALL) {
                return true;
            }
            return itemStack.getItem() instanceof ArmorItem ? mode == DeathDurabilityMode.ARMOR : mode == DeathDurabilityMode.TOOLS;
        }

        public static boolean shouldHideDurabilityBar(ItemStack itemStack) {
            if (!ServerConfigs.DURABILITY_MODULE_ENABLED.get()) {
                return false;
            }
            return (!shouldTakeDeathDamage(itemStack) || ServerConfigs.ADDITIONAL_DURABILITY_LOST_ON_DEATH.get() == 0 && ServerConfigs.DURABILITY_LOST_ON_DEATH.get() == 0) && (!shouldTakeVanillaDamage(itemStack));
        }
    }

}
