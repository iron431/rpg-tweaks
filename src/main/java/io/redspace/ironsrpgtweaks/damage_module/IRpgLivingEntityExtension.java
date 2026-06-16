package io.redspace.ironsrpgtweaks.damage_module;

import it.unimi.dsi.fastutil.objects.Object2LongMap;
import net.minecraft.core.Holder;
import net.minecraft.world.damagesource.DamageType;

public interface IRpgLivingEntityExtension {
    /**
     * @return Map of damage type to the game time when damage for this type was actually applied
     */
    Object2LongMap<Holder<DamageType>> rpg_tweaks$getHurtTracker();
    /**
     * @return Map of damage type to the game time when damage for this type was requested to be applied
     */
    Object2LongMap<Holder<DamageType>> rpg_tweaks$getRequestDamageTracker();

    void rpg_tweaks$updateLastRequest(Holder<DamageType> type, long timestamp);
    void rpg_tweaks$updateLastHurt(Holder<DamageType> type, long timestamp);
    void rpg_tweaks$garbageCollect(long gameTime);
    int rpg_tweaks$getLastBlockHurtTime();
    void rpg_tweaks$setLastBlockHurtTime(int gameTime);
}
