package io.redspace.ironsrpgtweaks.mixin;

import io.redspace.ironsrpgtweaks.config.ConfigHelper;
import io.redspace.ironsrpgtweaks.config.ServerConfigs;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FoodData.class)
public class FoodDataMixin {

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
    public void heal(ServerPlayer player, CallbackInfo ci) {
        if (!ConfigHelper.Hunger.shouldDisableVanillaHunger()) {
            return;
        }
        if (rpg_tweaks$toHeal > 0) {
            if (player.hasEffect(MobEffects.HUNGER)) {
                player.heal(rpg_tweaks$toHeal * ServerConfigs.HUNGER_NUTRITION_MULTIPLIER.get().floatValue());
            } else {
                player.heal(rpg_tweaks$toHeal);
            }
            rpg_tweaks$toHeal = 0;
        }
    }
}
