package io.redspace.ironsrpgtweaks.mixin;

import io.redspace.ironsrpgtweaks.config.ConfigHelper;
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
    public void irons_rpg_tweaks$cancelDurabilityUsage(int pAmount, RandomSource pRandom, @Nullable ServerPlayer pUser, CallbackInfoReturnable<Boolean> cir) {
        var self = (ItemStack) (Object) this;
        if (ConfigHelper.Durability.shouldTakeVanillaDamage(self)) {
            // if we still want vanilla damage functionality, stop
            return;
        }
        // if the full damage will break the item, let it pass so the game automatically handles the item breaking pipeline. otherwise, cancel damage
        if (self.getDamageValue() < self.getMaxDamage()) {
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "isBarVisible", at = @At(value = "HEAD"), cancellable = true)
    public void irons_rpg_tweaks$hideDurabilityBar(CallbackInfoReturnable<Boolean> cir) {
        if (ConfigHelper.Durability.shouldHideDurabilityBar((ItemStack) (Object) this)) {
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "getUseDuration", at = @At(value = "RETURN"), cancellable = true)
    public void irons_rpg_tweaks$getUseDuration(CallbackInfoReturnable<Integer> cir) {
        var d = ConfigHelper.Hunger.useDurationMultiplier((ItemStack) (Object) this);
        if (d != 1) {
            cir.setReturnValue((int) (cir.getReturnValue() * d));
        }
    }
}
