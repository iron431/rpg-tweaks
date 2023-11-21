package io.redspace.ironsrpgtweaks.damage_module;

import io.redspace.ironsrpgtweaks.config.ConfigHelper;
import io.redspace.ironsrpgtweaks.config.ServerConfigs;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingKnockBackEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;

@Mod.EventBusSubscriber
public class DamageServerEvents {

    public static final List<String> BLACKLIST_DAMAGE_SOURCES = List.of("lava", "inFire", "cactus", "inWall", "hotFloor", "lightningBolt", "sweetBerryBush", "outOfWorld", "drown");
    public static final List<String> BLACKLIST_ENTITY_TYPES = List.of("minecraft:slime", "minecraft:ender_dragon", "minecraft:magma_cube", "irons_spellbooks:wall_of_fire", "irons_spellbooks:void_tentacle");

    @SubscribeEvent
    public static void onTakeDamage(LivingDamageEvent event) {
        //IronsRpgTweaks.LOGGER.debug("{} took damage! ({})", event.getEntity().getName().getString(), event.getSource().getMsgId());

        if (ServerConfigs.DAMAGE_MODULE_ENABLED.get()) {
            if (event.getEntity() instanceof Player &&
                    ServerConfigs.PLAYER_DAMAGE_MODE.get() == PlayerDamageMode.NONE ||
                    (ServerConfigs.PLAYER_DAMAGE_MODE.get() == PlayerDamageMode.ONLY_LIVING && !(event.getSource().getDirectEntity() instanceof LivingEntity))
            ) {
                return;
            }
            if (testDamageSource(event.getSource())) {
                event.getEntity().invulnerableTime = ServerConfigs.IFRAME_COUNT.get();
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerAttack(AttackEntityEvent event) {
        var player = event.getEntity();
        if (player.getLevel().isClientSide) {
            return;
        }
        if (player.getAttackStrengthScale(0) < ServerConfigs.MINIMUM_ATTACK_STRENGTH.get()) {
            //IronsRpgTweaks.LOGGER.debug("DamageServerEvents.onPlayerAttack: cancelling");

            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void modifyKnockback(LivingKnockBackEvent event) {
        if (ServerConfigs.DAMAGE_MODULE_ENABLED.get()) {
            event.setStrength((float) (event.getStrength() * ServerConfigs.KNOCKBACK_MODIFIER.get()));
        }
    }

    private static boolean testDamageSource(DamageSource source) {

        //Some damage sources rely on damage tick to apply dot. We therefore do not want to cancel the damage tick in these cases
        if (ServerConfigs.DAMAGE_MODULE_DAMAGE_SOURCE_BLACKLIST.get().contains(source.getMsgId())) {
            return false;
        }
        return (source.getEntity() == null || !ConfigHelper.Damage.damageEntityBlacklist.contains(source.getEntity().getType())) &&
                (source.getDirectEntity() == null || !ConfigHelper.Damage.damageEntityBlacklist.contains(source.getDirectEntity().getType()));
    }

//    @SubscribeEvent
//    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
//        IronsRpgTweaks.LOGGER.debug("onPlayerLoggedIn");
//        if (event.getEntity() instanceof ServerPlayer serverPlayer) {
//            Messages.sendToPlayer(new ClientboundSyncConfig(CommonConfigs.DAMAGE_MODULE_ENABLED.get(), CommonConfigs.ALLOW_NON_FULL_STRENGTH_ATTACKS.get(), CommonConfigs.MINIMUM_ATTACK_STRENGTH.get()), serverPlayer);
//        }
//    }
}
