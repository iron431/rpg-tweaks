package io.redspace.ironsrpgtweaks.config;

import io.redspace.ironsrpgtweaks.IronsRpgTweaks;
import io.redspace.ironsrpgtweaks.hunger_module.RegistryGetter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.PotionItem;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;

import java.util.stream.Collectors;

@Mod.EventBusSubscriber(modid = IronsRpgTweaks.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ConfigEvents {

    @SubscribeEvent
    public static void onReload(ModConfigEvent event) {
        //IronsRpgTweaks.LOGGER.debug("ConfigEvents.onReload");
        if (event.getConfig().getType().equals(ModConfig.Type.SERVER)) {
            ConfigHelper.Damage.damageEntityBlacklist = ServerConfigs.DAMAGE_MODULE_ENTITY_BLACKLIST.get().stream()
                    .map(itemName -> ForgeRegistries.ENTITY_TYPES.getValue(new ResourceLocation(itemName)))
                    .collect(Collectors.toSet());
            ConfigHelper.Hunger.foodStackBlacklist = ServerConfigs.FOOD_STACK_BLACKLIST.get().stream()
                    .map(itemName -> ForgeRegistries.ITEMS.getValue(new ResourceLocation(itemName)))
                    .collect(Collectors.toSet());

            if (ServerConfigs.HUNGER_MODULE_ENABLED.get()) {
                int potionStack = Math.min(ServerConfigs.POTION_STACK_SIZE_OVERRIDE.get(), 64);
                int foodStack = Math.min(ServerConfigs.FOOD_STACK_SIZE_OVERRIDE.get(), 64);
                if (foodStack <= 0 && potionStack <= 0)
                    return;
                IForgeRegistry<Item> registry = RegistryGetter.getItem();
                registry.forEach((item) -> {
                    if (!ConfigHelper.Hunger.foodStackBlacklist.contains(item)) {
                        if (potionStack > 0 && item instanceof PotionItem) {
                            item.maxStackSize = potionStack;
                        } else if (foodStack > 0 && item.getFoodProperties() != null) {
                            item.maxStackSize = Math.min(item.maxStackSize, foodStack);
                        }
                    }
                });
            }
        }
    }
}
