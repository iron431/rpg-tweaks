package io.redspace.ironsrpgtweaks.hunger_module;

import static io.redspace.ironsrpgtweaks.registry.AttributeRegistry.*;

import io.redspace.ironsrpgtweaks.config.ConfigHelper;
import io.redspace.ironsrpgtweaks.config.ServerConfigs;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.level.GameRules;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class ServerHungerEvents {

    // Minecraft will send a packet to the client every time the hunger is updated (ServerPlayer:483), but only after sending the player tick event (ServerPlayer:469->Player:220)
    // If we modify the food in post, it should always be the same by the time it gets back to ServerPlayer. Therefore, no packet spamming
    @SubscribeEvent
    public static void setHunger(TickEvent.PlayerTickEvent event) {
        if (ConfigHelper.Hunger.shouldDisableVanillaHunger() && event.phase == TickEvent.Phase.END && event.side == LogicalSide.SERVER) {
            var player = event.player;
            var canSprint = !event.player.hasEffect(MobEffects.HUNGER);
            player.getFoodData().setFoodLevel(canSprint ? 10 : 5);

            if (Double.compare(player.getAttributeValue(NATURAL_REGEN_SPEED.get()), 0.0D) > 0) {
                int i = (int)(ServerConfigs.NATURAL_REGENERATION_TICK_RATE.get() / player.getAttributeValue(NATURAL_REGEN_SPEED.get()));
                if ((i <= 1 || player.tickCount % i == 0) && player.level().getGameRules().getBoolean(GameRules.RULE_NATURAL_REGENERATION) && (ServerConfigs.NATURAL_REGENERATION_DURING_COMBAT.get() || !player.getCombatTracker().inCombat))
                    player.heal((float)player.getAttributeValue(NATURAL_REGEN_AMOUNT.get()));
            }
        }
    }
}
