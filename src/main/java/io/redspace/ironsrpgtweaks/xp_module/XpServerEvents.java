package io.redspace.ironsrpgtweaks.xp_module;

import io.redspace.ironsrpgtweaks.entity.XpCatalyst;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingExperienceDropEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;
import java.util.List;

@Mod.EventBusSubscriber
public class XpServerEvents {

    @SubscribeEvent
    public static void onPlayerDeath(LivingDeathEvent event) {
        if (shouldCreateCatalyst())
            if (event.getEntity() instanceof ServerPlayer serverPlayer) {
                XpCatalyst.createXpCatalyst(serverPlayer);
                serverPlayer.setExperienceLevels(0);
                serverPlayer.setExperiencePoints(0);
            }
    }

    @SubscribeEvent
    public static void onXpDropped(LivingExperienceDropEvent event) {
        if (shouldCreateCatalyst())
            if (event.getEntity() instanceof ServerPlayer serverPlayer) {
                event.setCanceled(true);
            }
    }

    public static boolean shouldCreateCatalyst() {
        return true;
    }

}
