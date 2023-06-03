package io.redspace.ironsrpgtweaks.enchantment_module;

import net.minecraft.ChatFormatting;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import javax.annotation.Nullable;
import java.util.List;

@Mod.EventBusSubscriber(Dist.CLIENT)
public class EnchantmentClientEvents {
    public static final ResourceLocation ENCHANT_FONT = new ResourceLocation("alt");

    @SubscribeEvent
    public static void modifyTooltip(ItemTooltipEvent event) {
        ItemStack stack = event.getItemStack();
        ListTag enchants = getEnchantments(stack);
        var tooltipComponents = event.getToolTip();
        //TODO: checks for performance
        if (enchants != null) {
            if (shouldHideEnchantments(stack))
                for (int i = 0; i < enchants.size(); i++) {
                    CompoundTag compoundtag = enchants.getCompound(i);
                    Registry.ENCHANTMENT.getOptional(EnchantmentHelper.getEnchantmentId(compoundtag)).ifPresent((enchant) -> {
                        var component = enchant.getFullname(EnchantmentHelper.getEnchantmentLevel(compoundtag));
                        int j = tooltipComponents.indexOf(component);
                        if (j >= 0)
                            ((MutableComponent) tooltipComponents.get(j)).setStyle(Style.EMPTY.withColor(ChatFormatting.GRAY).withFont(ENCHANT_FONT));
                    });
                }
        }
    }

//    public static void replaceEnchantmentNames(int firstIndex, List<Component> tooltipComponents, ListTag enchantments) {
//        for (int i = 0; i < enchantments.size(); i++) {
//            CompoundTag compoundtag = enchantments.getCompound(i);
//            Registry.ENCHANTMENT.getOptional(EnchantmentHelper.getEnchantmentId(compoundtag)).ifPresent((enchant) -> {
//                var component = enchant.getFullname(EnchantmentHelper.getEnchantmentLevel(compoundtag));
//                tooltipComponents.ind
//                for (int j = firstIndex; j < tooltipComponents.size(); j++) {
//
//                }
//            });
//        }
//    }

    public static boolean shouldHideEnchantments(ItemStack stack) {
        CompoundTag compoundtag = stack.getTag();
        if (compoundtag == null)
            return false;
        else if (compoundtag.contains("hideEnchantments"))
            return compoundtag.getBoolean("hideEnchantments");
        else return false;
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
}
