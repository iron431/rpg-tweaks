package io.redspace.damage_module;

import io.redspace.config.ServerConfigs;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.InputEvent;

@EventBusSubscriber(Dist.CLIENT)
public class DamageClientEvents {

    @SubscribeEvent
    public static void onPlayerAttack(InputEvent.InteractionKeyMappingTriggered event) {
        var player = Minecraft.getInstance().player;
        if (ServerConfigs.DAMAGE_MODULE_ENABLED.get() && !ServerConfigs.ALLOW_NON_FULL_STRENGTH_ATTACKS.get() && player != null && event.isAttack() && player.getAttackStrengthScale(0) < ServerConfigs.MINIMUM_ATTACK_STRENGTH.get()) {
            //IronsRpgTweaks.LOGGER.debug("DamageClientEvents.onPlayerAttack: cancelling");
            event.setSwingHand(false);
            event.setCanceled(true);
        }
    }
}
