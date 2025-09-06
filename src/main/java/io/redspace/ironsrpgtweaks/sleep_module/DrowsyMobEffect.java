package io.redspace.ironsrpgtweaks.sleep_module;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;

public class DrowsyMobEffect extends MobEffect implements ICustomMobEffectDescription {
    protected DrowsyMobEffect(MobEffectCategory category, int color) {
        super(category, color);
    }

    @Override
    public Component getDescriptionLine(MobEffectInstance instance) {
        return Component.translatable("effect.irons_rpg_tweaks.drowsy.description").withStyle(ChatFormatting.BLUE);
    }
}
