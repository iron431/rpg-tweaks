package io.redspace.damage_module;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.core.Holder;
import net.minecraft.world.damagesource.DamageType;

public interface IRpgLivingEntityExtension {
    /**
     * @return Map of damage type to the tick where damage for this type was actually applied
     */
    Object2IntMap<Holder<DamageType>> rpg_tweaks$getHurtTracker();
    /**
     * @return Map of damage type to the tick where damage for this type was requested to be applied
     */
    Object2IntMap<Holder<DamageType>> rpg_tweaks$getRequestDamageTracker();

    void rpg_tweaks$updateLastRequest(Holder<DamageType> type, int timestamp);
    void rpg_tweaks$updateLastHurt(Holder<DamageType> type, int timestamp);
    void rpg_tweaks$garbageCollect(int tickCount);
}
