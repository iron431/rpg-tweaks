package io.redspace.ironsrpgtweaks.damage_module;

import io.redspace.ironsrpgtweaks.IronsRpgTweaks;
import io.redspace.ironsrpgtweaks.config.ClientConfig;
import io.redspace.ironsrpgtweaks.config.ServerConfigs;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

@Mod.EventBusSubscriber(Dist.CLIENT)
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

    @SubscribeEvent
    public static void onEntityHurt(LivingDamageEvent event) {
        if (!ServerConfigs.DAMAGE_MODULE_ENABLED.get() || !ClientConfig.combatDebugMode.get())
            return;
        var attacker = getResourceLocation(event.getSource().getDirectEntity());
        var victim = getResourceLocation(event.getEntity());
        var damageSource = event.getSource();
        String message;
        if (attacker != null) {
            message = String.format("Attacker: %s | Victim: %s | DamageSource: %s", attacker, victim, damageSource.getMsgId());

        } else {
            message = String.format("Victim: %s | DamageSource: %s", victim, damageSource.getMsgId());
        }
        IronsRpgTweaks.LOGGER.info(message);
        Minecraft.getInstance().player.sendSystemMessage(Component.literal(message));
        //IronsRpgTweaks.LOGGER.debug("{} took damage! ({})", event.getEntity().getName().getString(), event.getSource().getMsgId());

    }

    @Nullable
    public static ResourceLocation getResourceLocation(@Nullable Entity entity) {
        if (entity == null)
            return null;
        return ForgeRegistries.ENTITY_TYPES.getKey(entity.getType());
    }
}
