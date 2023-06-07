package io.redspace.ironsrpgtweaks.enchantment_module;

import io.redspace.ironsrpgtweaks.registry.SoundRegistry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
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

    public static void unhideEnchantments(ItemStack stack, @Nullable Entity entity) {
        if (shouldHideEnchantments(stack)) {
            stack.getOrCreateTag().putBoolean("hideEnchantments", false);
            if (entity != null) {
                entity.playSound(SoundRegistry.IDENTIFY.get());
            }
        }

    }

    public static void hideEnchantments(ItemStack stack) {
        stack.getOrCreateTag().putBoolean("hideEnchantments", true);
    }
}
