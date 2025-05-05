package io.redspace.ironsrpgtweaks.mixin;

import io.redspace.ironsrpgtweaks.damage_module.IRpgLivingEntityExtension;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.core.Holder;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(LivingEntity.class)
public class LivingEntityMixin implements IRpgLivingEntityExtension {
    @Unique
    Object2IntMap<Holder<DamageType>> rpg_tweaks$hurtTracker = new Object2IntOpenHashMap<>();
    @Unique
    Object2IntMap<Holder<DamageType>> rpg_tweaks$requestDamageTracker = new Object2IntOpenHashMap<>();

    @Override
    public Object2IntMap<Holder<DamageType>> rpg_tweaks$getHurtTracker() {
        return rpg_tweaks$hurtTracker;
    }

    @Override
    public Object2IntMap<Holder<DamageType>> rpg_tweaks$getRequestDamageTracker() {
        return rpg_tweaks$requestDamageTracker;
    }

    @Override
    public void rpg_tweaks$garbageCollect(int tickCount) {
        rpg_tweaks$hurtTracker.entrySet().removeIf(entry -> entry.getValue() < tickCount - 20);
        rpg_tweaks$requestDamageTracker.entrySet().removeIf(entry -> entry.getValue() < tickCount - 20);
    }

    @Override
    public void rpg_tweaks$updateLastRequest(Holder<DamageType> type, int timestamp) {
        rpg_tweaks$requestDamageTracker.put(type, timestamp);
    }

    @Override
    public void rpg_tweaks$updateLastHurt(Holder<DamageType> type, int timestamp) {
        rpg_tweaks$hurtTracker.put(type, timestamp);
    }
}
