package io.redspace.ironsrpgtweaks.mixin;

import io.redspace.ironsrpgtweaks.damage_module.IRpgLivingEntityExtension;
import it.unimi.dsi.fastutil.objects.Object2LongMap;
import it.unimi.dsi.fastutil.objects.Object2LongOpenHashMap;
import net.minecraft.core.Holder;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(LivingEntity.class)
public class LivingEntityMixin implements IRpgLivingEntityExtension {
    @Unique
    Object2LongMap<Holder<DamageType>> rpg_tweaks$hurtTracker = new Object2LongOpenHashMap<>();
    @Unique
    Object2LongMap<Holder<DamageType>> rpg_tweaks$requestDamageTracker = new Object2LongOpenHashMap<>();
    @Unique
    int rpg_tweaks$lastBlockHurtTime;

    @Override
    public Object2LongMap<Holder<DamageType>> rpg_tweaks$getHurtTracker() {
        return rpg_tweaks$hurtTracker;
    }

    @Override
    public Object2LongMap<Holder<DamageType>> rpg_tweaks$getRequestDamageTracker() {
        return rpg_tweaks$requestDamageTracker;
    }

    @Override
    public void rpg_tweaks$garbageCollect(long gameTime) {
        rpg_tweaks$hurtTracker.entrySet().removeIf(entry -> entry.getValue() < gameTime - 20);
        rpg_tweaks$requestDamageTracker.entrySet().removeIf(entry -> entry.getValue() < gameTime - 20);
    }

    @Override
    public int rpg_tweaks$getLastBlockHurtTime() {
        return this.rpg_tweaks$lastBlockHurtTime;
    }

    @Override
    public void rpg_tweaks$setLastBlockHurtTime(int gameTime) {
        this.rpg_tweaks$lastBlockHurtTime = gameTime;
    }

    @Override
    public void rpg_tweaks$updateLastRequest(Holder<DamageType> type, long timestamp) {
        rpg_tweaks$requestDamageTracker.put(type, timestamp);
    }

    @Override
    public void rpg_tweaks$updateLastHurt(Holder<DamageType> type, long timestamp) {
        rpg_tweaks$hurtTracker.put(type, timestamp);
    }
}
