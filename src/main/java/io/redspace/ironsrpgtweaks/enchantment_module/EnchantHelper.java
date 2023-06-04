package io.redspace.ironsrpgtweaks.enchantment_module;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;

public class EnchantHelper {

    public static boolean shouldHideEnchantments(ItemStack stack) {
        CompoundTag compoundtag = stack.getTag();
        if (compoundtag == null || !compoundtag.contains("hideEnchantments"))
            return false;
        else
            return compoundtag.getBoolean("hideEnchantments");
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

    public static void unhideEnchantments(ItemStack stack) {
        if (shouldHideEnchantments(stack))
            stack.getOrCreateTag().putBoolean("hideEnchantments", false);
    }

    public static void hideEnchantments(ItemStack stack) {
        stack.getOrCreateTag().putBoolean("hideEnchantments", true);
    }
}
