package io.redspace.ironsrpgtweaks.compat.farmers_delight;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;

public class FarmersDelightProxyImpl implements FarmersDelightProxy {
    private static final ResourceLocation NOURISHMENT = ResourceLocation.fromNamespaceAndPath("farmersdelight", "nourishment");
    private static final ResourceLocation COMFORT = ResourceLocation.fromNamespaceAndPath("farmersdelight", "comfort");

    @Override
    public boolean forceNaturalRegen(LivingEntity entity) {
        return BuiltInRegistries.MOB_EFFECT.getHolder(ResourceKey.create(Registries.MOB_EFFECT, COMFORT))
                .map(holder -> entity.hasEffect(holder.value()))
                .orElse(false);
    }

    @Override
    public float naturalRegenAmountMultiplier(LivingEntity entity) {
        return BuiltInRegistries.MOB_EFFECT.getHolder(ResourceKey.create(Registries.MOB_EFFECT, NOURISHMENT))
                .map(holder -> entity.hasEffect(holder.value())).map(hasNourishment -> hasNourishment ? 1.5f : 1f)
                .orElse(1f);
    }
}