package io.redspace.ironsrpgtweaks.xp_module;

import io.redspace.ironsrpgtweaks.IronsRpgTweaks;
import io.redspace.ironsrpgtweaks.config.CommonConfigs;
import io.redspace.ironsrpgtweaks.entity.XpCatalyst;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingExperienceDropEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;
import java.util.List;

@Mod.EventBusSubscriber
public class XpServerEvents {

    @SubscribeEvent
    public static void onPlayerDeath(LivingDeathEvent event) {
        if (event.getEntity() instanceof ServerPlayer serverPlayer) {
            IronsRpgTweaks.LOGGER.debug("Levels: {}\nPoints: {}\nTotal: {}", serverPlayer.experienceLevel, serverPlayer.experienceProgress, serverPlayer.totalExperience);
            if (shouldCreateCatalyst(serverPlayer.level)) {
                XpCatalyst.createXpCatalyst(serverPlayer);
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        if (event.getEntity() instanceof ServerPlayer serverPlayer && shouldCreateCatalyst(serverPlayer.level)) {
            serverPlayer.setExperienceLevels(0);
            serverPlayer.setExperiencePoints(0);
        }
    }

    @SubscribeEvent
    public static void onXpDropped(LivingExperienceDropEvent event) {
        if (event.getEntity() instanceof ServerPlayer serverPlayer) {
            if (shouldCreateCatalyst(serverPlayer.level)) {
//                int i = 0;
//                if (CommonConfigs.XP_DROP_REWARD_XP.get()) {
//                    event.setDroppedExperience(i);
//                }
//                event.setDroppedExperience(i);
                event.setCanceled(true);
            }
        }
    }

    public static int getVanillaXpReward(ServerPlayer serverPlayer) {
        int i = serverPlayer.experienceLevel * 7;
        return Math.min(i, 100);
    }

    public static boolean shouldCreateCatalyst(Level level) {
        return CommonConfigs.XP_MODULE_ENABLED.get()
                && (!CommonConfigs.XP_RESPECT_KEEPINVENTORY.get() || !level.getGameRules().getBoolean(GameRules.RULE_KEEPINVENTORY));

    }

}
