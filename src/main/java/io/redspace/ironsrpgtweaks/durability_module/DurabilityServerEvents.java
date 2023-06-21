package io.redspace.ironsrpgtweaks.durability_module;

import io.redspace.ironsrpgtweaks.config.ServerConfigs;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;
import java.util.List;

@Mod.EventBusSubscriber
public class DurabilityServerEvents {

//    @SubscribeEvent
//    public static void onTakeDamage(LivingDamageEvent event) {
//
//    }

    @SubscribeEvent
    public static void onPlayerDeath(LivingDeathEvent event) {
        if (event.getEntity() instanceof ServerPlayer serverPlayer) {
            if ((ServerConfigs.DURABILITY_LOST_ON_DEATH.get() == 0 && ServerConfigs.ADDITIONAL_DURABILITY_LOST_ON_DEATH.get() == 0) || !ServerConfigs.DURABILITY_MODULE_ENABLED.get() || serverPlayer.gameMode.isCreative())
                return;
            //IronsRpgTweaks.LOGGER.debug("{} died! ({})", serverPlayer.getName().getString(), printInventory(serverPlayer.getInventory()));
            DurabilityMode mode = ServerConfigs.DURABILITY_MODE.get();
            var inventory = serverPlayer.getInventory();
            if (mode.shouldDamageTools())
                damageItems(getHotbarItems(inventory), serverPlayer);
            if (mode.shouldDamageArmor())
                damageItems(getArmorItems(inventory), serverPlayer);
        }
    }

    private static void damageItems(List<ItemStack> items, ServerPlayer serverPlayer) {
        items.forEach((itemstack) -> {
            //IronsRpgTweaks.LOGGER.debug("{}", itemstack.getHoverName().getString());
            if (itemstack.isDamageableItem()) {
                int i = itemstack.getEnchantmentLevel(Enchantments.UNBREAKING) + 1;
                int damageAmount = (int) (itemstack.getMaxDamage() * ServerConfigs.DURABILITY_LOST_ON_DEATH.get()) + ServerConfigs.ADDITIONAL_DURABILITY_LOST_ON_DEATH.get();
                damageAmount /= i;
                itemstack.setDamageValue(itemstack.getDamageValue() + damageAmount);
                if (itemstack.getDamageValue() < itemstack.getMaxDamage()) {
                    if (itemstack.getMaxDamage() - itemstack.getDamageValue() < damageAmount)
                        serverPlayer.sendSystemMessage(Component.translatable("ui.irons_rpg_tweaks.item_damaged_critical", ((MutableComponent) itemstack.getDisplayName()).withStyle(Style.EMPTY.withColor(ChatFormatting.YELLOW).withItalic(false)), damageAmount).setStyle(Style.EMPTY.withColor(ChatFormatting.YELLOW).withItalic(true)));
                    else
                        serverPlayer.sendSystemMessage(Component.translatable("ui.irons_rpg_tweaks.item_damaged", ((MutableComponent) itemstack.getDisplayName()).withStyle(Style.EMPTY.withColor(ChatFormatting.GRAY).withItalic(false)), damageAmount).setStyle(Style.EMPTY.withColor(ChatFormatting.GRAY).withItalic(true)));
                }
                itemstack.hurtAndBreak(0, serverPlayer, (player) -> {
                    //TODO: sounds/particles of non-mainhand items too
                    player.sendSystemMessage(Component.translatable("ui.irons_rpg_tweaks.item_broken", ((MutableComponent) itemstack.getDisplayName()).withStyle(Style.EMPTY.withColor(ChatFormatting.RED).withItalic(false))).setStyle(Style.EMPTY.withColor(ChatFormatting.RED).withItalic(true)));
                    if (itemstack.getItem() instanceof ArmorItem armorItem)
                        player.broadcastBreakEvent(armorItem.getSlot());
                    else
                        player.broadcastBreakEvent(EquipmentSlot.MAINHAND);

                });
            }
        });
    }

    private static List<ItemStack> getHotbarItems(Inventory inventory) {
        List<ItemStack> hotbarItems = new ArrayList<>();
        for (int i = 0; i < inventory.getContainerSize() && i < 9; i++) {
            var item = inventory.getItem(i);
            if (!item.isEmpty())
                hotbarItems.add(item);
        }
        if (!inventory.offhand.get(0).isEmpty())
            hotbarItems.add(inventory.offhand.get(0));
        return hotbarItems;
    }

    private static List<ItemStack> getArmorItems(Inventory inventory) {
        List<ItemStack> armorItems = new ArrayList<>();
        for (int i = 0; i < inventory.armor.size(); i++) {
            var item = inventory.armor.get(i);
            if (!item.isEmpty())
                armorItems.add(item);
        }
        return armorItems;
    }

    private static String printInventory(Inventory inventory) {
        String str = "";
        for (int i = 0; i < inventory.getContainerSize(); i++) {
            str += inventory.getItem(i).getHoverName().getString() + ",";
        }
        return str;
    }

}
