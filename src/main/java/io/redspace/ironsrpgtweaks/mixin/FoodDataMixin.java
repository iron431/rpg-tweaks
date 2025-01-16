package io.redspace.ironsrpgtweaks.mixin;

import io.redspace.ironsrpgtweaks.config.ConfigHelper;
import io.redspace.ironsrpgtweaks.config.ServerConfigs;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FoodData.class)
public class FoodDataMixin {

    @Shadow
    int foodLevel;
    @Shadow
    int lastFoodLevel;

    @Unique
    float rpg_tweaks$toHeal;

    @Inject(method = "Lnet/minecraft/world/food/FoodData;add(IF)V", remap = false, at = @At(value = "HEAD"), cancellable = true)
    public void hungerToHealth(int nutritionToAdd, float saturationToAdd, CallbackInfo ci) {
        if (!ConfigHelper.Hunger.shouldDisableVanillaHunger()) {
            return;
        }
        rpg_tweaks$toHeal = (float) (nutritionToAdd * ServerConfigs.FOOD_TO_HEALTH_MODIFIER.get());
        ci.cancel();
    }

    @Inject(method = "tick", at = @At(value = "HEAD"))
    public void heal(Player pPlayer, CallbackInfo ci) {
        if (!ConfigHelper.Hunger.shouldDisableVanillaHunger()) {
            return;
        }
        if (rpg_tweaks$toHeal > 0) {
            pPlayer.heal(rpg_tweaks$toHeal);
            rpg_tweaks$toHeal = 0;
        }
//        if (lastFoodLevel != foodLevel) {
//            float healing = (float) ((foodLevel - lastFoodLevel) * ServerConfigs.FOOD_TO_HEALTH_MODIFIER.get() * .5f);
//            pPlayer.heal(healing);
//        }
    }
}
