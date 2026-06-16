package io.redspace.ironsrpgtweaks.compat.farmers_delight;

import net.minecraft.world.entity.LivingEntity;

public interface FarmersDelightProxy {

    default boolean forceNaturalRegen(LivingEntity entity) {
        return false;
    }

    default float naturalRegenAmountMultiplier(LivingEntity entity) {
        return 1f;
    }
}
