package io.redspace.ironsrpgtweaks.durability_module;

import io.redspace.ironsrpgtweaks.config.ConfigHelper;
import io.redspace.ironsrpgtweaks.config.ServerConfigs;
import net.minecraft.ChatFormatting;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.equipment.Equippable;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantments;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;

import java.util.ArrayList;
import java.util.List;

@EventBusSubscriber
public class DurabilityServerEvents {
    @SubscribeEvent
    public static void onPlayerDeath(LivingDeathEvent event) {
        if (event.getEntity() instanceof ServerPlayer serverPlayer) {
            if ((ServerConfigs.DURABILITY_LOST_ON_DEATH.get() == 0 && ServerConfigs.ADDITIONAL_DURABILITY_LOST_ON_DEATH.get() == 0) || !ServerConfigs.DURABILITY_MODULE_ENABLED.get() || serverPlayer.gameMode.isCreative()) {
                return;
            }
            //IronsRpgTweaks.LOGGER.debug("{} died! ({})", serverPlayer.getName().getString(), printInventory(serverPlayer.getInventory()));
            DeathDurabilityMode mode = ServerConfigs.DURABILITY_DEATH_MODE.get();
            var inventory = serverPlayer.getInventory();
            if (mode.shouldDamageTools()) {
                damageItems(getHotbarItems(inventory), serverPlayer);
            }
            if (mode.shouldDamageArmor()) {
                damageItems(getArmorItems(serverPlayer), serverPlayer);
            }
        }
    }

    private static void damageItems(List<ItemStack> items, ServerPlayer serverPlayer) {
        for (ItemStack itemstack : items) {
            //IronsRpgTweaks.LOGGER.debug("{}", itemstack.getHoverName().getString());
            if (itemstack.isDamageableItem() && ConfigHelper.Durability.shouldTakeDeathDamage(itemstack)) {
                int i = getUnbreakingDivisor(itemstack, serverPlayer.registryAccess());
                int damageAmount = (int) (itemstack.getMaxDamage() * ServerConfigs.DURABILITY_LOST_ON_DEATH.get()) + ServerConfigs.ADDITIONAL_DURABILITY_LOST_ON_DEATH.get();
                damageAmount /= i;
                itemstack.setDamageValue(itemstack.getDamageValue() + damageAmount);
                if (itemstack.getDisplayName() instanceof MutableComponent itemName) {
                    if (itemstack.getDamageValue() < itemstack.getMaxDamage()) {
                        if (itemstack.getMaxDamage() - itemstack.getDamageValue() < damageAmount) {
                            serverPlayer.sendSystemMessage(Component.translatable("ui.irons_rpg_tweaks.item_damaged_critical", itemName.withStyle(Style.EMPTY.withColor(ChatFormatting.YELLOW).withItalic(false)), damageAmount).setStyle(Style.EMPTY.withColor(ChatFormatting.YELLOW).withItalic(true)));
                        } else {
                            serverPlayer.sendSystemMessage(Component.translatable("ui.irons_rpg_tweaks.item_damaged", itemName.withStyle(Style.EMPTY.withColor(ChatFormatting.GRAY).withItalic(false)), damageAmount).setStyle(Style.EMPTY.withColor(ChatFormatting.GRAY).withItalic(true)));
                        }
                    }
                    if(serverPlayer.level() instanceof ServerLevel serverLevel){
                        itemstack.hurtAndBreak(0, serverLevel, serverPlayer, (item) -> {
                            serverPlayer.sendSystemMessage(Component.translatable("ui.irons_rpg_tweaks.item_broken", itemName.withStyle(Style.EMPTY.withColor(ChatFormatting.RED).withItalic(false))).setStyle(Style.EMPTY.withColor(ChatFormatting.RED).withItalic(true)));
                            Equippable equippable = itemstack.get(DataComponents.EQUIPPABLE);
                            EquipmentSlot slot = equippable != null ? equippable.slot() : EquipmentSlot.MAINHAND;
                            serverPlayer.onEquippedItemBroken(item, slot);
                        });
                    }
                }
            }
        }
    }

    private static int getUnbreakingDivisor(ItemStack itemstack, RegistryAccess access) {
        int i = 1;
        var registry = access.lookupOrThrow(Registries.ENCHANTMENT);
        var unbreaking = registry.get(Enchantments.UNBREAKING);
        if (unbreaking.isPresent()) {
            i += itemstack.getEnchantmentLevel(unbreaking.get());
        }
        return i;
    }

    private static List<ItemStack> getHotbarItems(Inventory inventory) {
        List<ItemStack> hotbarItems = new ArrayList<>();
        for (int i = 0; i < inventory.getContainerSize() && i < 9; i++) {
            var item = inventory.getItem(i);
            if (!item.isEmpty()) {
                hotbarItems.add(item);
            }
        }
        if (!inventory.getItem(Inventory.SLOT_OFFHAND).isEmpty()) {
            hotbarItems.add(inventory.getItem(Inventory.SLOT_OFFHAND));
        }
        return hotbarItems;
    }

    private static List<ItemStack> getArmorItems(ServerPlayer player) {
        List<ItemStack> armorItems = new ArrayList<>();
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            if (slot.getType() == EquipmentSlot.Type.HUMANOID_ARMOR) {
                var item = player.getItemBySlot(slot);
                if (!item.isEmpty()) {
                    armorItems.add(item);
                }
            }
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
