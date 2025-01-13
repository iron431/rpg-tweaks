package io.redspace.ironsrpgtweaks.damage_module;

import java.util.List;

import io.redspace.ironsrpgtweaks.config.ServerConfigs;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingKnockBackEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class DamageServerEvents {
    public static final List<String> BLACKLIST_DAMAGE_SOURCES = List.of("lava", "inFire", "cactus", "inWall", "hotFloor", "lightningBolt", "sweetBerryBush", "outOfWorld", "drown");
    public static final List<String> BLACKLIST_ENTITY_TYPES = List.of("minecraft:slime", "minecraft:ender_dragon", "minecraft:magma_cube", "irons_spellbooks:wall_of_fire", "irons_spellbooks:void_tentacle");

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
        long time = serverLevel.getGameTime();
        var livingExtension = (IRpgLivingEntityExtension) entity;
        int lastActuallyHurtTimestamp = livingExtension.rpg_tweaks$getHurtTracker().getOrDefault(source.typeHolder(), -1);
        int lastDamageRequestTimestamp = livingExtension.rpg_tweaks$getRequestDamageTracker().getOrDefault(source.typeHolder(), -1);
        int currentTick = entity.tickCount;
        // ignore the damage if we are requesting it to be taken every tick (delta ticks <= 1), unless full vanilla delay has passed since we actually last took this type of damage (20 ticks)
        boolean ignoreDamage =  /*legacyTestDamageSource(event.getSource()) &&*/ event.getEntity().invulnerableTime > 0 ||
                (currentTick - lastDamageRequestTimestamp <= 1 && currentTick - lastActuallyHurtTimestamp < 20);
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

    private static boolean legacyTestDamageSource(DamageSource source) {
        //Some damage sources rely on damage tick to apply dot. We therefore do not want to cancel the damage tick in these cases
        if (ServerConfigs.DAMAGE_MODULE_DAMAGE_SOURCE_BLACKLIST.get().contains(source.getMsgId())) {
            return false;
        }
        return (source.getEntity() == null || !ServerConfigs.RegistryLists.DAMAGE_ENTITY_BLACKLIST.contains(source.getEntity().getType())) &&
                (source.getDirectEntity() == null || !ServerConfigs.RegistryLists.DAMAGE_ENTITY_BLACKLIST.contains(source.getDirectEntity().getType()));
    }
}
