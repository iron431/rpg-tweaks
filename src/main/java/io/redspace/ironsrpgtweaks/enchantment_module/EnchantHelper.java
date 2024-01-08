package io.redspace.ironsrpgtweaks.enchantment_module;

import io.redspace.ironsrpgtweaks.config.ConfigHelper;
import io.redspace.ironsrpgtweaks.config.ServerConfigs;
import io.redspace.ironsrpgtweaks.registry.SoundRegistry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class EnchantHelper {

    public static final String hideEnchantsNBT = "hideEnchantments";
    public static boolean shouldHideEnchantments(ItemStack stack) {
        CompoundTag compoundtag = stack.getTag();
        if (compoundtag == null || !compoundtag.contains(hideEnchantsNBT))
            return false;
        else
            return compoundtag.getBoolean(hideEnchantsNBT);
    }

    @Nullable
    public static ListTag getEnchantments(ItemStack stack) {
        CompoundTag compoundtag = stack.getTag();
        if (compoundtag == null)
            return null;
        if (compoundtag.contains("StoredEnchantments")) {
            return compoundtag.getList("StoredEnchantments", 10);
        } else if (compoundtag.contains("Enchantments"))
            return compoundtag.getList("Enchantments", 10);
        return null;
    }

    public static void unhideEnchantments(ItemStack stack, @Nullable Entity entity) {
        if (shouldHideEnchantments(stack)) {
            stack.getOrCreateTag().putBoolean(hideEnchantsNBT, false);
            if (entity != null && ServerConfigs.ENCHANT_MODULE_ENABLED.get()) {
                entity.getLevel().playSound(null, entity, SoundRegistry.IDENTIFY.get(), SoundSource.PLAYERS, 1f, 1f);
            }
        }
    }

    public static void hideEnchantments(ItemStack stack) {
        stack.getOrCreateTag().putBoolean(hideEnchantsNBT, true);
    }

//    public static List<ItemStack> getEnchantedEquipmentItems(Mob mob) {
//        ArrayList<ItemStack> itemStacks = new ArrayList<>();
//        mob.getArmorSlots().forEach(itemStack -> {
//            if (isEquipmentItemEnchanted(itemStack)) {
//                itemStacks.add(itemStack);
//            }
//        });
//        if (isEquipmentItemEnchanted(mob.getMainHandItem())) {
//            itemStacks.add(mob.getMainHandItem());
//        }
//        if (isEquipmentItemEnchanted(mob.getOffhandItem())) {
//            itemStacks.add(mob.getOffhandItem());
//        }
//        return itemStacks;
//    }

    public static boolean isEquipmentItemEnchanted(ItemStack itemStack) {
        return itemStack.hasTag() && itemStack.getTag().contains("Enchantments");
    }
}
