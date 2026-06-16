package io.redspace.ironsrpgtweaks.compat.farmers_delight;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;

public class FarmersDelightProxyImpl implements FarmersDelightProxy {
    private static final ResourceLocation NOURISHMENT = ResourceLocation.fromNamespaceAndPath("farmersdelight", "nourishment");
    private static final ResourceLocation COMFORT = ResourceLocation.fromNamespaceAndPath("farmersdelight", "comfort");

    @Override
    public boolean forceNaturalRegen(LivingEntity entity) {
        return BuiltInRegistries.MOB_EFFECT.getHolder(COMFORT)
                .map(entity::hasEffect)
                .orElse(false);
    }

    @Override
    public float naturalRegenAmountMultiplier(LivingEntity entity) {
        return BuiltInRegistries.MOB_EFFECT.getHolder(NOURISHMENT)
                .map(entity::hasEffect).map(hasNourishment -> hasNourishment ? 1.5f : 1f)
                .orElse(1f);
    }
}