package io.redspace.ironsrpgtweaks.hunger_module;

import io.redspace.ironsrpgtweaks.config.ServerConfigs;
import net.minecraft.world.item.PotionItem;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.config.ModConfigEvent;

public class CommonHungerEvents {

    public static void changeStackSize(ModConfigEvent event) {
        if (event.getConfig().getType().equals(ModConfig.Type.SERVER)) {
            if (!ServerConfigs.HUNGER_MODULE_ENABLED.get())
                return;

            int potionStack = Math.min(ServerConfigs.POTION_STACK_SIZE_OVERRIDE.get(), 64);
            int foodStack = Math.min(ServerConfigs.FOOD_STACK_SIZE_OVERRIDE.get(), 64);
            if (foodStack <= 0 && potionStack <= 0)
                return;
            RegistryGetter.iterateItems((item) -> {
                if (potionStack > 0 && item instanceof PotionItem)
                    item.maxStackSize = potionStack;
                else if (foodStack > 0 && item.getFoodProperties() != null)
                    item.maxStackSize = Math.min(item.maxStackSize, foodStack);
                //IronsRpgTweaks.LOGGER.debug("changeStackSize {} {}", item.getDescriptionId(), item.getMaxStackSize());

            });
        }

    }
}
