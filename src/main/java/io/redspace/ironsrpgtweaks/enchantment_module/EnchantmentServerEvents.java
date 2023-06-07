package io.redspace.ironsrpgtweaks.enchantment_module;

import io.redspace.ironsrpgtweaks.IronsRpgTweaks;
import io.redspace.ironsrpgtweaks.config.CommonConfigs;
import net.minecraft.ChatFormatting;
import net.minecraft.client.model.IronGolemModel;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundSetActionBarTextPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.EntityDamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.EnchantmentMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.event.entity.living.*;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.PlayerContainerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;
import java.util.List;

@Mod.EventBusSubscriber
public class EnchantmentServerEvents {

    @SubscribeEvent
    public static void identifyOnEquip(LivingEquipmentChangeEvent event) {
        if (!CommonConfigs.IDENTIFY_ON_EQUIP.get() || !(event.getEntity() instanceof Player))
            return;
        var slot = event.getSlot();
        if (slot == EquipmentSlot.HEAD || slot == EquipmentSlot.CHEST || slot == EquipmentSlot.LEGS || slot == EquipmentSlot.FEET) {
            EnchantHelper.unhideEnchantments(event.getTo(), event.getEntity());
        }
    }

    @SubscribeEvent
    public static void disableEnchantmentTableInteract(PlayerInteractEvent.RightClickBlock event) {
        if (CommonConfigs.ENCHANT_MODULE_ENABLED.get() && CommonConfigs.DISABLE_ENCHANTING_TABLE.get())
            if (event.getEntity().level.getBlockState(event.getHitVec().getBlockPos()).is(Blocks.ENCHANTING_TABLE)) {
                event.setCanceled(true);
                if (event.getEntity() instanceof ServerPlayer serverPlayer)
                    serverPlayer.connection.send(new ClientboundSetActionBarTextPacket(Component.translatable("ui.irons_rpg_tweaks.enchanting_table_error").withStyle(ChatFormatting.RED)));
            }
    }

    @SubscribeEvent
    public static void hideOnMobSpawn(LivingSpawnEvent event) {
        EnchantHelper.getEnchantedEquipmentItems(event.getEntity()).forEach((itemStack) -> {
            //IronsRpgTweaks.LOGGER.debug("LivingSpawnEvent.equipment {}",itemStack.getHoverName().getString());
            if (!itemStack.getOrCreateTag().contains(EnchantHelper.hideEnchantsNBT)) {
                EnchantHelper.hideEnchantments(itemStack);
                //IronsRpgTweaks.LOGGER.debug("LivingSpawnEvent hiding enchanmtents",itemStack.getHoverName().getString());

            }
        });
    }
}
