package io.redspace.ironsrpgtweaks.mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.InsideBlockEffectApplier;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockBehaviour;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(BlockBehaviour.BlockStateBase.class)
public class BlockBehaviorMixin {

    @WrapMethod(method = "entityInside")
    private void wrapBlockDamage(Level level, BlockPos pos, Entity entity, InsideBlockEffectApplier effectApplier, boolean isPrecise, Operation<Void> original) {
        if (entity == null || !(entity instanceof LivingEntity livingEntity)) {
            original.call(level, pos, entity, effectApplier, isPrecise);
            return;
        }
        int hurtTime = livingEntity.hurtTime;
        original.call(level, pos, entity, effectApplier, isPrecise);
        if (hurtTime != livingEntity.hurtTime) {
            // both entity tick and entity motion creates "entity inside" checks, resulting in multiple hurt calls per tick,
            // therefore, check for if the block hurt the entity, and if so, force normal iframes.
            livingEntity.invulnerableTime = 20;
        }
    }
}
