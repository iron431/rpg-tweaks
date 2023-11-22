package io.redspace.ironsrpgtweaks.hunger_module;

import io.redspace.ironsrpgtweaks.config.ConfigHelper;
import io.redspace.ironsrpgtweaks.config.ServerConfigs;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
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
        if (ConfigHelper.Hunger.disableVanillaHunger() && event.phase == TickEvent.Phase.END && event.side == LogicalSide.SERVER) {
            var player = event.player;
            var canSprint = !event.player.hasEffect(MobEffects.HUNGER);
            player.getFoodData().setFoodLevel(canSprint ? 10 : 5);

            int i = ServerConfigs.NATURAL_REGENERATION_TICK_RATE.get();
            if (player.tickCount % i == 0 && player.getLevel().getGameRules().getBoolean(GameRules.RULE_NATURAL_REGENERATION) && !player.getCombatTracker().inCombat)
                player.heal(1);
        }
    }
}
