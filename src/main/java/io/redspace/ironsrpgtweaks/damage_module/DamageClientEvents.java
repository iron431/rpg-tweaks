package io.redspace.ironsrpgtweaks.damage_module;

import io.redspace.ironsrpgtweaks.IronsRpgTweaks;
import io.redspace.ironsrpgtweaks.config.CommonConfigs;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(Dist.CLIENT)
public class DamageClientEvents {

    @SubscribeEvent
    public static void onPlayerAttack(InputEvent.InteractionKeyMappingTriggered event) {
        var player = Minecraft.getInstance().player;
        if (CommonConfigs.DAMAGE_MODULE_ENABLED.get()  && !CommonConfigs.ALLOW_NON_FULL_STRENGTH_ATTACKS.get() && player != null && event.isAttack() && player.getAttackStrengthScale(0) < CommonConfigs.MINIMUM_ATTACK_STRENGTH.get()) {
            //IronsRpgTweaks.LOGGER.debug("DamageClientEvents.onPlayerAttack: cancelling");
            event.setSwingHand(false);
            event.setCanceled(true);
        }
    }
}
