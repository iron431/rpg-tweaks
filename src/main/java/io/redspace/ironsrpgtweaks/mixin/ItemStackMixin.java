package io.redspace.ironsrpgtweaks.mixin;

import io.redspace.ironsrpgtweaks.config.ServerConfigs;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;

@Mixin(ItemStack.class)
public class ItemStackMixin {

    @Inject(method = "hurt", at = @At(value = "HEAD"), cancellable = true)
    public void cancelDurabilityUsage(int pAmount, RandomSource pRandom, @Nullable ServerPlayer pUser, CallbackInfoReturnable<Boolean> cir) {
        if (ServerConfigs.DURABILITY_MODULE_ENABLED.get() && !ServerConfigs.TAKE_DURABILITY_DAMAGE.get()) {
            var self = (ItemStack) (Object) this;
            if (self.getDamageValue() < self.getMaxDamage()) {
                //if we aren't going to break, ignore the damage. useful for manually doing damage on death.
                cir.setReturnValue(false);

            }
        }
    }
}
