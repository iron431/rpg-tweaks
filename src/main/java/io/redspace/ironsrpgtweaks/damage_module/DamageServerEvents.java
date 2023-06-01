package io.redspace.ironsrpgtweaks.damage_module;

import io.redspace.ironsrpgtweaks.IronsRpgTweaks;
import io.redspace.ironsrpgtweaks.config.CommonConfigs;
import io.redspace.ironsrpgtweaks.setup.Messages;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.EntityDamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;

@Mod.EventBusSubscriber
public class DamageServerEvents {

    public static final List<String> BLACKLIST_DAMAGE_SOURCES = List.of("lava", "inFire", "cactus", "inWall", "hotFloor", "lightningBolt", "sweetBerryBush", "outOfWorld");
    public static final List<String> BLACKLIST_ENTITY_TYPES = List.of("minecraft:slime", "minecraft:ender_dragon", "minecraft:magma_cube");

    @SubscribeEvent
    public static void onTakeDamage(LivingDamageEvent event) {
        //IronsRpgTweaks.LOGGER.debug("{} took damage! ({})", event.getEntity().getName().getString(), event.getSource().getMsgId());

        if (CommonConfigs.DAMAGE_MODULE_ENABLED.get() && testDamageSource(event.getSource()))
            event.getEntity().invulnerableTime = 0;
    }

    @SubscribeEvent
    public static void onPlayerAttack(AttackEntityEvent event) {
        var player = event.getEntity();
        if (player.level.isClientSide)
            return;
        if (player.getAttackStrengthScale(0) < CommonConfigs.MINIMUM_ATTACK_STRENGTH.get()) {
            IronsRpgTweaks.LOGGER.debug("DamageServerEvents.onPlayerAttack: cancelling");

            event.setCanceled(true);
        }
    }


    private static boolean testDamageSource(DamageSource source) {

        //Some damage sources rely on damage tick to apply dot. We therefore do not want to cancel the damage tick in these cases
        if (CommonConfigs.DAMAGE_MODULE_DAMAGE_SOURCE_BLACKLIST.get().contains(source.getMsgId()))
            return false;
        if (source instanceof EntityDamageSource entityDamageSource && entityDamageSource.getEntity() != null) {

            String entityType = EntityType.getKey(source.getEntity().getType()).toString();
            //IronsRpgTweaks.LOGGER.debug("entity type: {}",entityType);
            if (CommonConfigs.DAMAGE_MODULE_ENTITY_BLACKLIST.get().contains(entityType))
                return false;
        }

        return true;

    }

//    @SubscribeEvent
//    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
//        IronsRpgTweaks.LOGGER.debug("onPlayerLoggedIn");
//        if (event.getEntity() instanceof ServerPlayer serverPlayer) {
//            Messages.sendToPlayer(new ClientboundSyncConfig(CommonConfigs.DAMAGE_MODULE_ENABLED.get(), CommonConfigs.ALLOW_NON_FULL_STRENGTH_ATTACKS.get(), CommonConfigs.MINIMUM_ATTACK_STRENGTH.get()), serverPlayer);
//        }
//    }
}
