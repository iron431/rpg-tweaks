package io.redspace.ironsrpgtweaks.enchantment_module;

import io.redspace.ironsrpgtweaks.config.ServerConfigs;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundSetActionBarTextPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.event.entity.living.*;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class EnchantmentServerEvents {

    @SubscribeEvent
    public static void identifyOnEquip(LivingEquipmentChangeEvent event) {
        if (!ServerConfigs.IDENTIFY_ON_EQUIP.get() || !(event.getEntity() instanceof Player))
            return;
        var slot = event.getSlot();
        if (slot == EquipmentSlot.HEAD || slot == EquipmentSlot.CHEST || slot == EquipmentSlot.LEGS || slot == EquipmentSlot.FEET) {
            EnchantHelper.unhideEnchantments(event.getTo(), event.getEntity());
        }
    }

    @SubscribeEvent
    public static void disableEnchantmentTableInteract(PlayerInteractEvent.RightClickBlock event) {
        if (event.getEntity() instanceof ServerPlayer serverPlayer && event.getEntity().getLevel().getBlockState(event.getHitVec().getBlockPos()).is(Blocks.ENCHANTING_TABLE) && ServerConfigs.ENCHANT_MODULE_ENABLED.get()) {

            boolean canceled = ServerConfigs.DISABLE_ENCHANTING_TABLE.get();
            Component message = Component.translatable("ui.irons_rpg_tweaks.enchanting_table_error.disabled").withStyle(ChatFormatting.RED);

            if (ServerConfigs.IDENTIFY_ON_ENCHANTING_TABLE.get() && !event.getEntity().getItemInHand(event.getHand()).isEmpty()) {
                var itemStack = event.getEntity().getItemInHand(event.getHand());
                var enchanted = EnchantHelper.getEnchantments(itemStack) != null;
                var identified = !EnchantHelper.shouldHideEnchantments(itemStack);
                if (enchanted && identified)
                    message = Component.translatable("ui.irons_rpg_tweaks.enchanting_table_error.identified").withStyle(ChatFormatting.LIGHT_PURPLE);
                else if (enchanted && !identified){
                    EnchantHelper.unhideEnchantments(itemStack, serverPlayer);
                    message = Component.translatable("ui.irons_rpg_tweaks.enchanting_table_success", itemStack.getHoverName().getString()).withStyle(ChatFormatting.GREEN);
                    canceled = true;
                }
            }
            if (canceled) {
                event.setCanceled(true);
                serverPlayer.connection.send(new ClientboundSetActionBarTextPacket(message));

            }
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
