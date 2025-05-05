package io.redspace.ironsrpgtweaks.damage_module;

import io.redspace.ironsrpgtweaks.config.ServerConfigs;
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
        if (ServerConfigs.DAMAGE_MODULE_ENABLED.get() && !ServerConfigs.ALLOW_NON_MINIMUM_STRENGTH_ATTACKS.get() && player != null && event.isAttack() && player.getAttackStrengthScale(0) < ServerConfigs.MINIMUM_ATTACK_STRENGTH.get()) {
            // If Damage module's prevent non-min-strength attacks is enabled, and our attack delay has not crossed the threshold, cancel attack
            event.setSwingHand(false);
            event.setCanceled(true);
        }
    }
}
