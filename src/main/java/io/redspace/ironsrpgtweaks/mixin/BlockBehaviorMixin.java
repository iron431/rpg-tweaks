package io.redspace.ironsrpgtweaks.mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockBehaviour;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(BlockBehaviour.BlockStateBase.class)
public class BlockBehaviorMixin {

    @WrapMethod(method = "entityInside(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/entity/Entity;)V")
    private void wrapBlockDamage(Level level, BlockPos pos, Entity entity, Operation<Void> original) {
        if (entity == null || !(entity instanceof LivingEntity livingEntity)) {
            original.call(level, pos, entity);
            return;
        }
        int hurtTime = livingEntity.hurtTime;
        original.call(level, pos, entity);
        if (hurtTime != livingEntity.hurtTime) {
            // both entity tick and entity motion creates "entity inside" checks, resulting in multiple hurt calls per tick,
            // therefore, check for if the block hurt the entity, and if so, force normal iframes.
            livingEntity.invulnerableTime = 20;
        }
    }
}
