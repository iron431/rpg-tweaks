package io.redspace.ironsrpgtweaks.enchantment_module;

import io.redspace.ironsrpgtweaks.IronsRpgTweaks;
import io.redspace.ironsrpgtweaks.config.ServerConfigs;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Registry;
import net.minecraft.data.BuiltinRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.List;

@Mod.EventBusSubscriber(Dist.CLIENT)
public class EnchantmentClientEvents {
    public static final ResourceLocation ENCHANT_FONT = new ResourceLocation("alt");

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void modifyTooltip(ItemTooltipEvent event) {
        if (!ServerConfigs.ENCHANT_MODULE_ENABLED.get())
            return;
        ItemStack stack = event.getItemStack();
        ListTag enchants = EnchantHelper.getEnchantments(stack);
        var tooltipComponents = event.getToolTip();
        if (enchants != null) {
            if (EnchantHelper.shouldHideEnchantments(stack)) {
                for (int e = 0; e < enchants.size(); e++) {
                    CompoundTag compoundtag = enchants.getCompound(e);
                    Enchantment enchantment = ForgeRegistries.ENCHANTMENTS.getValue(EnchantmentHelper.getEnchantmentId(compoundtag));
                    if (enchantment != null) {
                        var enchantKey = enchantment.getDescriptionId();
                        for (int i = tooltipComponents.size() - 1; i >= 0; i--) {
                            if (tooltipComponents.get(i).getContents() instanceof TranslatableContents translatableContents) {
                                var tooltipKey = translatableContents.getKey();
                                if (tooltipKey.equals(enchantKey)) {
                                    //This is the enchantment, hide it
                                    ((MutableComponent) tooltipComponents.get(i)).setStyle(Style.EMPTY.withColor(ChatFormatting.GRAY).withFont(ENCHANT_FONT));
                                } else if (tooltipKey.contains(enchantKey)) {
                                    //this is something dependent on the enchantment, such as a description. remove it.
                                    tooltipComponents.remove(i);
                                }
                            }
                        }
                    }
                }
            }
        }
        //if (event.getFlags().isAdvanced())
        //    tooltipComponents.replaceAll(component -> component.getContents() instanceof TranslatableContents ? Component.literal(((TranslatableContents) component.getContents()).getKey()).withStyle(Style.EMPTY.withColor(component.getStyle().getColor())) : component);
    }
}
