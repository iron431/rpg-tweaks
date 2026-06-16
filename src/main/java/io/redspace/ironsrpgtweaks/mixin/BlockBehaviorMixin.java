package io.redspace.ironsrpgtweaks.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockBehaviour;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BlockBehaviour.BlockStateBase.class)
public class BlockBehaviorMixin {

    @Unique
    private int rpg_tweaks$cachedHurtTime = -1;

    @Inject(method = "entityInside", at = @At("HEAD"))
    private void cacheHurtTime(Level level, BlockPos pos, Entity entity, CallbackInfo ci) {
        if (entity instanceof LivingEntity livingEntity) {
            rpg_tweaks$cachedHurtTime = livingEntity.hurtTime;
        } else {
            rpg_tweaks$cachedHurtTime = -1;
        }
    }

    @Inject(method = "entityInside", at = @At("RETURN"))
    private void restoreIframes(Level level, BlockPos pos, Entity entity, CallbackInfo ci) {
        if (entity instanceof LivingEntity livingEntity && rpg_tweaks$cachedHurtTime != -1 && rpg_tweaks$cachedHurtTime != livingEntity.hurtTime) {
            // both entity tick and entity motion creates "entity inside" checks, resulting in multiple hurt calls per tick,
            // therefore, check for if the block hurt the entity, and if so, force normal iframes.
            livingEntity.invulnerableTime = 20;
        }
    }
}
