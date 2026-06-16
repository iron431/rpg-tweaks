package io.redspace.ironsrpgtweaks.hunger_module;

import io.redspace.ironsrpgtweaks.compat.CompatHandler;
import io.redspace.ironsrpgtweaks.config.ConfigHelper;
import io.redspace.ironsrpgtweaks.config.ServerConfigs;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.GameRules;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import static io.redspace.ironsrpgtweaks.registry.AttributeRegistry.NATURAL_REGEN_AMOUNT;
import static io.redspace.ironsrpgtweaks.registry.AttributeRegistry.NATURAL_REGEN_SPEED;

@Mod.EventBusSubscriber
public class ServerHungerEvents {

    @SubscribeEvent
    public static void handleHungerTicking(TickEvent.PlayerTickEvent event) {
        if (ConfigHelper.Hunger.shouldDisableVanillaHunger() && event.player instanceof ServerPlayer player) {
            player.getFoodData().setFoodLevel(10);
            if (Double.compare(player.getAttributeValue(NATURAL_REGEN_SPEED.get()), 0.0D) > 0) {
                int regenTickDelay = (int) (ServerConfigs.NATURAL_REGENERATION_TICK_RATE.get() / player.getAttributeValue(NATURAL_REGEN_SPEED.get()));
                if ((regenTickDelay <= 1 || player.tickCount % regenTickDelay == 0)) {
                    boolean allowedToRegen =
                            CompatHandler.FARMERS_DELIGHT_PROXY.forceNaturalRegen(player) ||
                                    (player.level().getGameRules().getBoolean(GameRules.RULE_NATURAL_REGENERATION) &&
                                            (ServerConfigs.NATURAL_REGENERATION_DURING_COMBAT.get() || !player.getCombatTracker().inCombat));
                    if (allowedToRegen) {
                        player.heal((float) player.getAttributeValue(NATURAL_REGEN_AMOUNT.get()) * CompatHandler.FARMERS_DELIGHT_PROXY.naturalRegenAmountMultiplier(player));
                    }
                }
            }
        }
    }
}
