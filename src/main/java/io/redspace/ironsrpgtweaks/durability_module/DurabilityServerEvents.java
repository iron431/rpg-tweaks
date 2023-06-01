package io.redspace.ironsrpgtweaks.durability_module;

import io.redspace.ironsrpgtweaks.IronsRpgTweaks;
import io.redspace.ironsrpgtweaks.config.CommonConfigs;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.EntityDamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
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
//
//    @SubscribeEvent
//    public static void onPlayerDeath(LivingDeathEvent event) {
//        if (event.getEntity() instanceof ServerPlayer serverPlayer) {
//            //IronsRpgTweaks.LOGGER.debug("{} died! ({})", serverPlayer.getName().getString(), printInventory(serverPlayer.getInventory()));
//            var inventory = serverPlayer.getInventory();
//            List<ItemStack> activeItems = new ArrayList<>();
//            activeItems.addAll(getHotbarItems(inventory));
//            activeItems.addAll(getArmorItems(inventory));
//            activeItems.forEach((itemstack) -> {
//                //IronsRpgTweaks.LOGGER.debug("{}", itemstack.getHoverName().getString());
//                if(itemstack.isDamageableItem())
//                    itemstack.setDamageValue();
//            });
//        }
//    }

    private static List<ItemStack> getHotbarItems(Inventory inventory) {
        List<ItemStack> hotbarItems = new ArrayList<>();
        for (int i = 0; i < inventory.getContainerSize() && i < 9; i++) {
            var item = inventory.getItem(i);
            if (!item.isEmpty())
                hotbarItems.add(item);
        }
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
