package io.redspace.ironsrpgtweaks.config;

import io.redspace.ironsrpgtweaks.durability_module.DeathDurabilityMode;
import io.redspace.ironsrpgtweaks.durability_module.VanillaDurabilityMode;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PotionItem;
import net.minecraft.world.item.equipment.Equippable;

public class ConfigHelper {
    public static class Durability {
        public static boolean shouldTakeVanillaDamage(ItemStack itemStack) {
            if (!ServerConfigs.DURABILITY_MODULE_ENABLED.get()) {
                return true;
            }
            // Check whitelist/blacklist before "mode == NONE" because mitigating user error is more important than critical perfection
            if (ServerConfigs.RegistryLists.DURABILITY_VANILLA_MODE_BLACKLIST_ITEMS.contains(itemStack.getItem())) {
                return false;
            }
            if (!ServerConfigs.RegistryLists.DURABILITY_VANILLA_MODE_WHITELIST_ITEMS.isEmpty()) {
                return ServerConfigs.RegistryLists.DURABILITY_VANILLA_MODE_WHITELIST_ITEMS.contains(itemStack.getItem());
            }
            var mode = ServerConfigs.DURABILITY_VANILLA_MODE.get();
            if (mode == VanillaDurabilityMode.NONE) {
                return false;
            }
            if (mode == VanillaDurabilityMode.ALL) {
                return true;
            }
            return isArmor(itemStack) ? mode == VanillaDurabilityMode.ARMOR : mode == VanillaDurabilityMode.TOOLS;
        }

        public static boolean shouldTakeDeathDamage(ItemStack itemStack) {
            if (!ServerConfigs.DURABILITY_MODULE_ENABLED.get()) {
                return false;
            }
            // Check whitelist/blacklist before "mode == NONE" because mitigating user error is more important than critical perfection
            if (ServerConfigs.RegistryLists.DURABILITY_DEATH_MODE_BLACKLIST_ITEMS.contains(itemStack.getItem())) {
                return false;
            }
            if (!ServerConfigs.RegistryLists.DURABILITY_DEATH_MODE_WHITELIST_ITEMS.isEmpty()) {
                return ServerConfigs.RegistryLists.DURABILITY_DEATH_MODE_WHITELIST_ITEMS.contains(itemStack.getItem());
            }
            DeathDurabilityMode mode = ServerConfigs.DURABILITY_DEATH_MODE.get();
            if (mode == DeathDurabilityMode.NONE) {
                return false;
            }
            if (mode == DeathDurabilityMode.ALL) {
                return true;
            }
            return isArmor(itemStack) ? mode == DeathDurabilityMode.ARMOR : mode == DeathDurabilityMode.TOOLS;
        }

        public static boolean shouldHideDurabilityBar(ItemStack itemStack) {
            if (!ServerConfigs.DURABILITY_MODULE_ENABLED.get()) {
                return false;
            }
            return (!shouldTakeDeathDamage(itemStack) || (ServerConfigs.ADDITIONAL_DURABILITY_LOST_ON_DEATH.get() == 0 && ServerConfigs.DURABILITY_LOST_ON_DEATH.get() == 0))
                    && (!shouldTakeVanillaDamage(itemStack));
        }

        private static boolean isArmor(ItemStack itemStack) {
            Equippable equippable = itemStack.get(DataComponents.EQUIPPABLE);
            return equippable != null && equippable.slot().getType() == EquipmentSlot.Type.HUMANOID_ARMOR;
        }
    }

    public static class Damage {
    }

    public static class Hunger {
        public static double useDurationMultiplier(ItemStack item) {
            if (ServerConfigs.HUNGER_MODULE_ENABLED.get()) {
                if (item.has(DataComponents.FOOD)) {
                    return ServerConfigs.EAT_TIME_MULTIPLIER.get();
                } else if (item.getItem() instanceof PotionItem) {
                    return ServerConfigs.POTION_DRINK_TIME_MULTIPLER.get();
                }
            }
            return 1.0;
        }

        public static boolean canSprint(LivingEntity entity) {
            return (!ServerConfigs.HUNGER_PREVENTS_SPRINTING.get() || !entity.hasEffect(MobEffects.HUNGER)) && (ServerConfigs.ALLOW_SPRINTING.get() || entity.isUnderWater());
        }

        public static boolean shouldDisableVanillaHunger() {
            return ServerConfigs.HUNGER_DISABLED.get() && ServerConfigs.HUNGER_MODULE_ENABLED.get();
        }
    }
}
