package io.redspace.ironsrpgtweaks.mixin;

import io.redspace.ironsrpgtweaks.config.ConfigHelper;
import io.redspace.ironsrpgtweaks.config.ServerConfigs;
import net.minecraft.core.component.PatchedDataComponentMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.Consumer;

@Mixin(ItemStack.class)
public class ItemStackMixin {

    @Inject(method = "Lnet/minecraft/world/item/ItemStack;<init>(Lnet/minecraft/world/level/ItemLike;ILnet/minecraft/core/component/PatchedDataComponentMap;)V", at = @At(value = "RETURN"))
    void irons_rpg_tweaks$applyDefaultStackComponents(ItemLike item, int count, PatchedDataComponentMap components, CallbackInfo ci) {
        ServerConfigs.handleDefaultStackComponents((ItemStack) (Object) this);
    }

    @Inject(method = "hurtAndBreak(ILnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/entity/LivingEntity;Ljava/util/function/Consumer;)V", at = @At(value = "HEAD"), cancellable = true)
    public void irons_rpg_tweaks$cancelDurabilityUsage(int pAmount, ServerLevel serverLevel, LivingEntity pUser, Consumer<Item> onBreak, CallbackInfo cir) {
        var self = (ItemStack) (Object) this;
        // if the full damage will break the item, let it pass so the game automatically handles the item breaking pipeline. otherwise, cancel damage
        boolean isGoingToBreak = self.getDamageValue() >= self.getMaxDamage();
        if (!ConfigHelper.Durability.shouldTakeVanillaDamage(self) && !isGoingToBreak) {
            cir.cancel();
        }
    }

    @Inject(method = "isBarVisible", at = @At(value = "HEAD"), cancellable = true)
    public void irons_rpg_tweaks$hideDurabilityBar(CallbackInfoReturnable<Boolean> cir) {
        if (ConfigHelper.Durability.shouldHideDurabilityBar((ItemStack) (Object) this)) {
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "getUseDuration", at = @At(value = "RETURN"), cancellable = true)
    public void irons_rpg_tweaks$getUseDuration(LivingEntity entity, CallbackInfoReturnable<Integer> cir) {
        var d = ConfigHelper.Hunger.useDurationMultiplier((ItemStack) (Object) this);
        if (d != 1) {
            cir.setReturnValue((int) (cir.getReturnValue() * d));
        }
    }
}
