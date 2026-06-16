package io.redspace.ironsrpgtweaks.sleep_module;

import io.redspace.ironsrpgtweaks.config.ServerConfigs;
import io.redspace.ironsrpgtweaks.registry.PotionEffectsRegistry;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BedItem;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.CanPlayerSleepEvent;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;
import net.neoforged.neoforge.event.level.SleepFinishedTimeEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

@EventBusSubscriber
public class SleepCommonEvents {

    @SubscribeEvent
    public static void preventPlayerSleep(CanPlayerSleepEvent event) {
        if (!ServerConfigs.SLEEP_MODULE_ENABLED.get()) {
            return;
        }
        if (!event.getEntity().hasEffect(PotionEffectsRegistry.DROWSY_EFFECT) && !event.getEntity().isCreative()) {
            if (event.getProblem() == null) {
                event.setProblem(Player.BedSleepingProblem.OTHER_PROBLEM);
                event.getEntity().sendSystemMessage(Component.translatable("ui.irons_rpg_tweaks.sleep_failure").withStyle(ChatFormatting.RED), true);
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
    public static void tickNaturalDrowsiness(PlayerTickEvent.Post event) {
        if (!ServerConfigs.SLEEP_MODULE_ENABLED.get() || !(event.getEntity() instanceof ServerPlayer serverPlayer)) {
            return;
        }
        int drowsinessDelay = ServerConfigs.NATURAL_DROWSINESS_DELAY.get();
        if (drowsinessDelay <= 0) {
            return;
        }
        if (serverPlayer.tickCount % (20 * 60) == 0 && !serverPlayer.isCreative()) {
            int timeSinceLastRest = Mth.clamp(serverPlayer.getStats().getValue(Stats.CUSTOM.get(Stats.TIME_SINCE_REST)), 1, Integer.MAX_VALUE);
            if (timeSinceLastRest > drowsinessDelay && !serverPlayer.hasEffect(PotionEffectsRegistry.DROWSY_EFFECT)) {
                serverPlayer.addEffect(new MobEffectInstance(PotionEffectsRegistry.DROWSY_EFFECT, 20 * 60 * 20, 0, false, false, true));
                serverPlayer.sendSystemMessage(Component.translatable("ui.irons_rpg_tweaks.natural_drowsy_message").withColor(0xAAAAFF), true);
            }
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
