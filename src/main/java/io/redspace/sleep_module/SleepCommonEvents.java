package io.redspace.sleep_module;

import com.google.gson.annotations.Since;
import io.redspace.config.ServerConfigs;
import io.redspace.registry.PotionEffectsRegistry;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BedItem;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.CanPlayerSleepEvent;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;
import net.neoforged.neoforge.event.level.SleepFinishedTimeEvent;

@EventBusSubscriber
public class SleepCommonEvents {

    @SubscribeEvent
    public static void preventPlayerSleep(CanPlayerSleepEvent event) {
        if (!ServerConfigs.SLEEP_MODULE_ENABLED.get()) {
            return;
        }
        if (!event.getEntity().hasEffect(PotionEffectsRegistry.DROWSY_EFFECT)) {
            if (event.getProblem() == null) {
                event.setProblem(Player.BedSleepingProblem.OTHER_PROBLEM);
                event.getEntity().displayClientMessage(Component.translatable("ui.irons_rpg_tweaks.sleep_failure").withStyle(ChatFormatting.RED), true);
            }
        }
    }

    @SubscribeEvent
    public static void clearDrowsiness(SleepFinishedTimeEvent event) {
        var server = event.getLevel().getServer();
        if (server != null) {
            server.getPlayerList().getPlayers().forEach(player -> player.removeEffect(PotionEffectsRegistry.DROWSY_EFFECT));
        }
    }

    @SubscribeEvent
    public static void handleTooltips(ItemTooltipEvent event) {
        if (!ServerConfigs.SLEEP_MODULE_ENABLED.get()) {
            return;
        }
        ItemStack stack = event.getItemStack();
        var potionData = stack.get(DataComponents.POTION_CONTENTS);
        if (potionData != null) {
            potionData.getAllEffects().forEach(mobEffectInstance -> {
                if (mobEffectInstance.getEffect().value() instanceof ICustomMobEffectDescription customDescriptionMobEffect) {
                    ICustomMobEffectDescription.handleCustomPotionTooltip(stack, event.getToolTip(), event.getFlags().isAdvanced(), mobEffectInstance, customDescriptionMobEffect);
                }
            });
        }
        if (stack.getItem() instanceof BedItem bedItem) {
            event.getToolTip().add(1, Component.translatable("ui.irons_rpg_tweaks.bed_tooltip").withStyle(ChatFormatting.GOLD));
        }
    }
}
