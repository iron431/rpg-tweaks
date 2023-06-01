package io.redspace.ironsrpgtweaks.mixin;

import io.redspace.ironsrpgtweaks.IronsRpgTweaks;
import io.redspace.ironsrpgtweaks.config.CommonConfigs;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemStack.class)
public class ItemStackMixin {

    @Inject(method = "hurt", at = @At(value = "HEAD"), cancellable = true)
    public void cancelDurabilityUsage(CallbackInfoReturnable<Boolean> cir) {
        IronsRpgTweaks.LOGGER.debug("MIXING WORKED");
        if (CommonConfigs.DURABILITY_MODULE_ENABLED.get() && !CommonConfigs.TAKE_DURABILITY_DAMAGE.get()) {
            cir.setReturnValue(false);
        }
    }
}
