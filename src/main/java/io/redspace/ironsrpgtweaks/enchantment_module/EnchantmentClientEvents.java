package io.redspace.ironsrpgtweaks.enchantment_module;

import io.redspace.ironsrpgtweaks.config.CommonConfigs;
import io.redspace.ironsrpgtweaks.enchantment_module.EnchantHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(Dist.CLIENT)
public class EnchantmentClientEvents {
    public static final ResourceLocation ENCHANT_FONT = new ResourceLocation("alt");

    @SubscribeEvent
    public static void modifyTooltip(ItemTooltipEvent event) {
        //TODO... client can just disable it :skull:
        if (!CommonConfigs.ENCHANT_MODULE_ENABLED.get())
            return;
        ItemStack stack = event.getItemStack();
        ListTag enchants = EnchantHelper.getEnchantments(stack);
        var tooltipComponents = event.getToolTip();
        //TODO: checks for performance. Mixin would probably be way more performant
        if (enchants != null) {
            if (EnchantHelper.shouldHideEnchantments(stack))
                for (int i = 0; i < enchants.size(); i++) {
                    CompoundTag compoundtag = enchants.getCompound(i);
                    BuiltInRegistries.f_256876_.getOptional(EnchantmentHelper.getEnchantmentId(compoundtag)).ifPresent((enchant) -> {
                        var component = enchant.getFullname(EnchantmentHelper.getEnchantmentLevel(compoundtag));
                        int j = tooltipComponents.indexOf(component);
                        if (j >= 0)
                            ((MutableComponent) tooltipComponents.get(j)).setStyle(Style.EMPTY.withColor(ChatFormatting.GRAY).withFont(ENCHANT_FONT));
                    });

                }
        }
    }

}
