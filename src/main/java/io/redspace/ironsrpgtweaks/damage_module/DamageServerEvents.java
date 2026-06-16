package io.redspace.ironsrpgtweaks.damage_module;

import io.redspace.ironsrpgtweaks.config.ServerConfigs;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingKnockBackEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class DamageServerEvents {

    @SubscribeEvent(priority = EventPriority.LOWEST) // fire after all other modifier events from other mods
    public static void onIncomingDamage(LivingAttackEvent event) {
        if (ServerConfigs.DAMAGE_MODULE_ENABLED.get()) {
            var source = event.getSource();
            var entity = event.getEntity();
            if (!(entity.level() instanceof ServerLevel serverLevel)) {
                return;
            }
            long currentGameTime = serverLevel.getGameTime();
            if (!shouldProcess(source, entity)) {
                return;
            }
            var livingExtension = (IRpgLivingEntityExtension) entity;
            long lastActuallyHurtTimestamp = livingExtension.rpg_tweaks$getHurtTracker().getOrDefault(source.typeHolder(), -1L);
            long lastDamageRequestTimestamp = livingExtension.rpg_tweaks$getRequestDamageTracker().getOrDefault(source.typeHolder(), -1L);
            long ticksSinceDamaged = currentGameTime - lastActuallyHurtTimestamp;
            long ticksSinceAttemptedDamage = currentGameTime - lastDamageRequestTimestamp;
            if (ticksSinceDamaged == 1 || (ticksSinceAttemptedDamage == 1 && ticksSinceDamaged < 10)) {
                // block sequential tick damage, up to a full duration of 10 ticks
                livingExtension.rpg_tweaks$updateLastRequest(source.typeHolder(), currentGameTime);
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public static void onTakeDamage(LivingDamageEvent event) {
        if (ServerConfigs.DAMAGE_MODULE_ENABLED.get()) {
            if (shouldProcess(event.getSource(), event.getEntity())) {
                event.getEntity().invulnerableTime = ServerConfigs.IFRAME_COUNT.get();
                IRpgLivingEntityExtension entityExtension = (IRpgLivingEntityExtension) event.getEntity();
                entityExtension.rpg_tweaks$updateLastHurt(event.getSource().typeHolder(), event.getEntity().level().getGameTime());
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerAttack(AttackEntityEvent event) {
        if (ServerConfigs.DAMAGE_MODULE_ENABLED.get()) {

            Level level = event.getEntity().level();
            if (level.isClientSide) {
                return;
            }
            if (!(event.getEntity() instanceof FakePlayer) && event.getEntity().getAttackStrengthScale(0) < ServerConfigs.MINIMUM_ATTACK_STRENGTH.get()) {
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public static void modifyKnockback(LivingKnockBackEvent event) {
        if (ServerConfigs.DAMAGE_MODULE_ENABLED.get()) {
            event.setStrength((float) (event.getStrength() * ServerConfigs.KNOCKBACK_MODIFIER.get()));
        }
    }

    @SubscribeEvent
    public static void onLivingTick(LivingEvent.LivingTickEvent event) {
        if (ServerConfigs.DAMAGE_MODULE_ENABLED.get() && event.getEntity().level().getGameTime() % 600 == 0) {
            ((IRpgLivingEntityExtension) event.getEntity()).rpg_tweaks$garbageCollect(event.getEntity().level().getGameTime());
        }
    }

    private static boolean shouldProcess(DamageSource source, LivingEntity entityBeingAttacked) {
        if (ServerConfigs.DAMAGE_MODULE_ENABLED.get()) {
            return
                    (source.getDirectEntity() == null || ServerConfigs.RegistryLists.ENTITY_IFRAME_BLACKLIST.isEmpty() || !ServerConfigs.RegistryLists.ENTITY_IFRAME_BLACKLIST.contains(source.getDirectEntity().getType())) &&
                            (!(entityBeingAttacked instanceof Player)
                                    || ServerConfigs.PLAYER_DAMAGE_MODE.get() == PlayerDamageMode.ALL
                                    || (ServerConfigs.PLAYER_DAMAGE_MODE.get() == PlayerDamageMode.ONLY_LIVING && (source.getDirectEntity() instanceof LivingEntity)));
        }
        return false;
    }
}
