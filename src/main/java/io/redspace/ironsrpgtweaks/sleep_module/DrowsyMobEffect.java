package io.redspace.ironsrpgtweaks.sleep_module;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

public class DrowsyMobEffect extends MobEffect implements ICustomMobEffectDescription {
    protected DrowsyMobEffect(MobEffectCategory category, int color) {
        super(category, color);
    }

    @Override
    public Component getDescriptionLine(MobEffectInstance instance) {
        return Component.translatable("effect.irons_rpg_tweaks.drowsy.description").withStyle(ChatFormatting.BLUE);
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return duration % 100 == 0;
    }

    @Override
    public boolean applyEffectTick(@NotNull LivingEntity livingEntity, int amplifier) {
        if (livingEntity.level().dimensionType().hasFixedTime() || livingEntity.level().isDay()) {
            // remove drownsiess from player during the day, or in dimensions without day/night cycle
            return false;
        }
        return super.applyEffectTick(livingEntity, amplifier);
    }
}
