package io.redspace.ironsrpgtweaks.sleep_module;

import io.redspace.ironsrpgtweaks.config.ServerConfigs;
import io.redspace.ironsrpgtweaks.registry.PotionEffectsRegistry;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.BedItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.event.entity.player.SleepingTimeCheckEvent;
import net.minecraftforge.event.level.SleepFinishedTimeEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class SleepCommonEvents {

    //1.20.1: handled by ServerPlayerMixin
//    @SubscribeEvent
//    public static void preventPlayerSleep(SleepingTimeCheckEvent event) {
//        if (!ServerConfigs.SLEEP_MODULE_ENABLED.get()) {
//            return;
//        }
//        if (!event.getEntity().hasEffect(PotionEffectsRegistry.DROWSY_EFFECT.get()) && !event.getEntity().isCreative()) {
//            event.setResult(Event.Result.DENY);
//            event.getEntity().displayClientMessage(Component.translatable("ui.irons_rpg_tweaks.sleep_failure").withStyle(ChatFormatting.RED), true);
//        }
//    }

    @SubscribeEvent
    public static void clearDrowsiness(SleepFinishedTimeEvent event) {
        var server = event.getLevel().getServer();
        if (server != null) {
            server.getPlayerList().getPlayers().forEach(player -> player.removeEffect(PotionEffectsRegistry.DROWSY_EFFECT.get()));
        }
    }

    @SubscribeEvent
    public static void tickNaturalDrowsiness(TickEvent.PlayerTickEvent event) {
        if (!ServerConfigs.SLEEP_MODULE_ENABLED.get() || !(event.player instanceof ServerPlayer serverPlayer)) {
            return;
        }
        int drowsinessDelay = ServerConfigs.NATURAL_DROWSINESS_DELAY.get();
        if (drowsinessDelay <= 0) {
            return;
        }
        if (serverPlayer.tickCount % (20 * 60) == 0 && !serverPlayer.isCreative()) {
            int timeSinceLastRest = Mth.clamp(serverPlayer.getStats().getValue(Stats.CUSTOM.get(Stats.TIME_SINCE_REST)), 1, Integer.MAX_VALUE);
            if (timeSinceLastRest > drowsinessDelay && !serverPlayer.hasEffect(PotionEffectsRegistry.DROWSY_EFFECT.get())) {
                serverPlayer.addEffect(new MobEffectInstance(PotionEffectsRegistry.DROWSY_EFFECT.get(), 20 * 60 * 20, 0, false, false, true));
                serverPlayer.displayClientMessage(Component.translatable("ui.irons_rpg_tweaks.natural_drowsy_message").withStyle(Style.EMPTY.withColor(0xAAAAFF)), true);
            }
        }
    }

    @SubscribeEvent
    public static void handleTooltips(ItemTooltipEvent event) {
        if (!ServerConfigs.SLEEP_MODULE_ENABLED.get()) {
            return;
        }
        ItemStack stack = event.getItemStack();
        if (!stack.hasTag()) {
            return;
        }
        PotionUtils.getAllEffects(stack.getTag())
                .forEach(mobEffectInstance -> {
                    if (mobEffectInstance.getEffect() instanceof ICustomMobEffectDescription customDescriptionMobEffect) {
                        ICustomMobEffectDescription.handleCustomPotionTooltip(stack, event.getToolTip(), event.getFlags().isAdvanced(), mobEffectInstance, customDescriptionMobEffect);
                    }
                });
        if (stack.getItem() instanceof BedItem bedItem) {
            event.getToolTip().add(1, Component.translatable("ui.irons_rpg_tweaks.bed_tooltip").withStyle(ChatFormatting.GOLD));
        }
    }
}
