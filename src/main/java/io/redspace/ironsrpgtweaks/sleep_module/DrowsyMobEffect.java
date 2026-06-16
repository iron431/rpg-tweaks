package io.redspace.ironsrpgtweaks.sleep_module;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

public class DrowsyMobEffect extends MobEffect implements ICustomMobEffectDescription {
    public DrowsyMobEffect(MobEffectCategory category, int color) {
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
    public boolean applyEffectTick(@NotNull ServerLevel serverLevel, @NotNull LivingEntity livingEntity, int amplifier) {
        if (livingEntity.level().dimensionType().hasFixedTime()) {
            return false;
        }
        long dayTime = serverLevel.getDefaultClockTime() % 24000L;
        if (dayTime < 12000L) {
            // remove drowsiness from player during the day
            return false;
        }
        return super.applyEffectTick(serverLevel, livingEntity, amplifier);
    }
}
