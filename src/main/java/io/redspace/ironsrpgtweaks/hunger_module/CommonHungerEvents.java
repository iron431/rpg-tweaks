package io.redspace.ironsrpgtweaks.hunger_module;

import io.redspace.ironsrpgtweaks.config.ServerConfigs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.PotionItem;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;

import java.util.List;

public class CommonHungerEvents {

    public static final List<String> DEFAULT_FOOD_BLACKLIST = List.of("minecraft:rotten_flesh", "minecraft:spider_eye", "minecraft:potato", "minecraft:carrot", "farmersdelight:onion", "farmersdelight:tomato", "farmersdelight:cabbage");

    public static void changeStackSize(ModConfigEvent event) {
        if (event.getConfig().getType().equals(ModConfig.Type.SERVER)) {
            if (!ServerConfigs.HUNGER_MODULE_ENABLED.get())
                return;

            int potionStack = Math.min(ServerConfigs.POTION_STACK_SIZE_OVERRIDE.get(), 64);
            int foodStack = Math.min(ServerConfigs.FOOD_STACK_SIZE_OVERRIDE.get(), 64);
            if (foodStack <= 0 && potionStack <= 0)
                return;
            IForgeRegistry<Item> registry = RegistryGetter.getItem();
            var blacklist = ServerConfigs.FOOD_STACK_BLACKLIST.get();
            registry.forEach((item) -> {
                if (!blacklist.contains(ForgeRegistries.ITEMS.getKey(item).toString()))
                    if (potionStack > 0 && item instanceof PotionItem)
                        item.maxStackSize = potionStack;
                    else if (foodStack > 0 && item.getFoodProperties() != null)
                        item.maxStackSize = Math.min(item.maxStackSize, foodStack);
            });
        }

    }
}
