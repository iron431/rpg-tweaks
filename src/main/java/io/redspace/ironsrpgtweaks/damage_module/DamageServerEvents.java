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
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class DamageServerEvents {

    @SubscribeEvent
    public static void onRecieveDamage(LivingAttackEvent event) {
        var source = event.getSource();
        var entity = event.getEntity();
        if (!(entity.level() instanceof ServerLevel serverLevel)) {
            return;
        }
        if (!shouldProcess(source, entity)) {
            return;
        }
        var livingExtension = (IRpgLivingEntityExtension) entity;
        int lastActuallyHurtTimestamp = livingExtension.rpg_tweaks$getHurtTracker().getOrDefault(source.typeHolder(), -1);
        int lastDamageRequestTimestamp = livingExtension.rpg_tweaks$getRequestDamageTracker().getOrDefault(source.typeHolder(), -1);
        int currentTick = entity.tickCount;
        // some damage types apply damage every tick use entity iframes to space out their damage, like lava or cactus
        // therefore, if we detect a source attempting to damage every tick, we want to ignore until the default tick delay has passed
        // ergo: ignore = requestDelta <= 1 && hurtDelta < 10
        boolean ignoreDamage = event.getEntity().invulnerableTime > 0 ||
                (currentTick - lastDamageRequestTimestamp <= 1 && currentTick - lastActuallyHurtTimestamp < 10);
        if (ignoreDamage) {
            event.setCanceled(true);
        }
        livingExtension.rpg_tweaks$updateLastRequest(source.typeHolder(), currentTick);
    }

    @SubscribeEvent
    public static void onTakeDamage(LivingDamageEvent event) {
        if (shouldProcess(event.getSource(), event.getEntity()) /*&& legacyTestDamageSource(event.getSource())*/) {
            event.getEntity().invulnerableTime = ServerConfigs.IFRAME_COUNT.get();
            IRpgLivingEntityExtension entityExtension = (IRpgLivingEntityExtension) event.getEntity();
            entityExtension.rpg_tweaks$updateLastHurt(event.getSource().typeHolder(), event.getEntity().tickCount);
        }
    }

    @SubscribeEvent
    public static void onPlayerAttack(AttackEntityEvent event) {
        Level level = event.getEntity().level();
        if (level.isClientSide) {
            return;
        }
        if (!(event.getEntity() instanceof FakePlayer) && event.getEntity().getAttackStrengthScale(0) < ServerConfigs.MINIMUM_ATTACK_STRENGTH.get()) {
            event.setCanceled(true);
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
        if (ServerConfigs.DAMAGE_MODULE_ENABLED.get() && event.getEntity().tickCount % 600 == 0) {
            ((IRpgLivingEntityExtension) event.getEntity()).rpg_tweaks$garbageCollect(event.getEntity().tickCount);
        }
    }

    private static boolean shouldProcess(DamageSource source, LivingEntity entityBeingAttacked) {
        if (ServerConfigs.DAMAGE_MODULE_ENABLED.get()) {
            return
                    !(entityBeingAttacked instanceof Player)
                            || ServerConfigs.PLAYER_DAMAGE_MODE.get() == PlayerDamageMode.ALL
                            || (ServerConfigs.PLAYER_DAMAGE_MODE.get() == PlayerDamageMode.ONLY_LIVING && (source.getDirectEntity() instanceof LivingEntity)
                    );
        }
        return false;
    }
}