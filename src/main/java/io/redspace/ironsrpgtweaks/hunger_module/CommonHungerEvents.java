package io.redspace.ironsrpgtweaks.hunger_module;

import io.redspace.ironsrpgtweaks.config.ServerConfigs;
import net.minecraft.world.item.Items;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;

@Mod.EventBusSubscriber
public class CommonHungerEvents {

    public static final List<String> DEFAULT_FOOD_BLACKLIST = List.of("minecraft:rotten_flesh", "minecraft:spider_eye", "minecraft:potato", "minecraft:carrot", "farmersdelight:onion", "farmersdelight:tomato", "farmersdelight:cabbage");

    @SubscribeEvent
    public static void handlePotionCooldowns(PlayerInteractEvent.RightClickItem event) {
        if (!ServerConfigs.HUNGER_MODULE_ENABLED.get()) {
            return;
        }
        var player = event.getEntity();
        if (event.getItemStack().is(Items.SPLASH_POTION) && ServerConfigs.SPLASH_POTION_COOLDOWN.get() > 0) {
            player.getCooldowns().addCooldown(Items.SPLASH_POTION, (int) (ServerConfigs.SPLASH_POTION_COOLDOWN.get() * 20));
        } else if (event.getItemStack().is(Items.LINGERING_POTION) && ServerConfigs.LINGERING_POTION_COOLDOWN.get() > 0) {
            player.getCooldowns().addCooldown(Items.LINGERING_POTION, (int) (ServerConfigs.LINGERING_POTION_COOLDOWN.get() * 20));
        }
    }

}
