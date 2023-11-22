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
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FoodData.class)
public class FoodDataMixin {

    @Shadow
    int foodLevel;
    @Shadow
    int lastFoodLevel;

    @Inject(method = "eat(Lnet/minecraft/world/item/Item;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/entity/LivingEntity;)V", remap = false, at = @At(value = "HEAD"), cancellable = true)
    public void hungerToHealth(Item pItem, ItemStack pStack, LivingEntity entity, CallbackInfo ci) {
        if (!ConfigHelper.Hunger.disableVanillaHunger())
            return;
        if (pItem.isEdible()) {
            FoodProperties foodproperties = pStack.getFoodProperties(entity);
            if (foodproperties != null && entity != null) {
                float healing = (float) (foodproperties.getNutrition() * ServerConfigs.FOOD_TO_HEALTH_MODIFIER.get());
                entity.heal(healing);
                lastFoodLevel = foodLevel;
                ci.cancel();
            }
        }
    }

    @Inject(method = "tick", at = @At(value = "HEAD"))
    public void detectEatCake(Player pPlayer, CallbackInfo ci) {
        if (!ConfigHelper.Hunger.disableVanillaHunger())
            return;
        if (lastFoodLevel != foodLevel) {
            float healing = (float) ((foodLevel - lastFoodLevel) * ServerConfigs.FOOD_TO_HEALTH_MODIFIER.get() * .5f);
            pPlayer.heal(healing);
        }
    }
}
