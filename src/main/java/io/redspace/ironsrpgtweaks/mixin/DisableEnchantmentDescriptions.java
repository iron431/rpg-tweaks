package io.redspace.ironsrpgtweaks.mixin;

import io.redspace.ironsrpgtweaks.enchantment_module.EnchantHelper;
import net.darkhax.enchdesc.EnchDescCommon;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(EnchDescCommon.class)
public class DisableEnchantmentDescriptions {

    @Inject(method = "onItemTooltip", at = @At(value = "HEAD"), cancellable = true, remap = false)
    private void onItemTooltip(ItemStack stack, List<Component> tooltip, TooltipFlag tooltipFlag, CallbackInfo ci) {
        if (EnchantHelper.shouldHideEnchantments(stack))
            ci.cancel();
    }
}
