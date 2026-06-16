package io.redspace.ironsrpgtweaks.hunger_module;

import io.redspace.ironsrpgtweaks.config.ServerConfigs;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingEntityUseItemEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

@EventBusSubscriber
public class CommonHungerEvents {

    /**
     * manually call event via on config reload
     */
    public static void modifyDefaultStackSize(BiConsumer<Holder<Item>, Consumer<DataComponentPatch.Builder>> modifyCallback) {
        if (!ServerConfigs.HUNGER_MODULE_ENABLED.get()) {
            return;
        }
        int food = ServerConfigs.FOOD_STACK_SIZE.get();
        int potions = ServerConfigs.POTION_STACK_SIZE.get();
        if (potions > 1) {
            modifyCallback.accept(BuiltInRegistries.ITEM.wrapAsHolder(Items.POTION), builder -> builder.set(DataComponents.MAX_STACK_SIZE, Math.min(potions, 99)));
            modifyCallback.accept(BuiltInRegistries.ITEM.wrapAsHolder(Items.SPLASH_POTION), builder -> builder.set(DataComponents.MAX_STACK_SIZE, Math.min(potions, 99)));
            modifyCallback.accept(BuiltInRegistries.ITEM.wrapAsHolder(Items.LINGERING_POTION), builder -> builder.set(DataComponents.MAX_STACK_SIZE, Math.min(potions, 99)));
        }
        if (food > 0) {
            for (Item item : BuiltInRegistries.ITEM) {
                var fooddata = item.components().get(DataComponents.FOOD);
                if (fooddata != null) {
                    // min nutrition = 5 to prevent resource-like foods (rotten flesh, raw carrot/potatoes, etc) from being limited
                    // however, ensure anything with positive effects (golden apples) is limited no matter the nutrition value
                    if (fooddata.nutrition() >= 5 || item.components().has(DataComponents.CONSUMABLE)) {
                        modifyCallback.accept(BuiltInRegistries.ITEM.wrapAsHolder(item), builder -> builder.set(DataComponents.MAX_STACK_SIZE, Math.min(food, Math.min(item.getDefaultMaxStackSize(), 99))));
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void handleThrownPotionCooldowns(PlayerInteractEvent.RightClickItem event) {
        if (!ServerConfigs.HUNGER_MODULE_ENABLED.get()) {
            return;
        }
        var player = event.getEntity();
        if (event.getItemStack().is(Items.SPLASH_POTION) && ServerConfigs.SPLASH_POTION_COOLDOWN.get() > 0) {
            player.getCooldowns().addCooldown(new ItemStack(Items.SPLASH_POTION), (int) (ServerConfigs.SPLASH_POTION_COOLDOWN.get() * 20));
        } else if (event.getItemStack().is(Items.LINGERING_POTION) && ServerConfigs.LINGERING_POTION_COOLDOWN.get() > 0) {
            player.getCooldowns().addCooldown(new ItemStack(Items.LINGERING_POTION), (int) (ServerConfigs.LINGERING_POTION_COOLDOWN.get() * 20));
        }
    }

    @SubscribeEvent
    public static void handleConsumableCooldowns(LivingEntityUseItemEvent.Finish event) {
        if (!ServerConfigs.HUNGER_MODULE_ENABLED.get()) {
            return;
        }
        if (event.getEntity() instanceof Player player) {
            var stack = event.getItem();
            if (stack.is(Items.POTION) && ServerConfigs.DRINKABLE_POTION_COOLDOWN.get() > 0 && stack.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY).hasEffects()) {
                player.getCooldowns().addCooldown(new ItemStack(Items.POTION), (int) (ServerConfigs.DRINKABLE_POTION_COOLDOWN.get() * 20));
            }
            if (stack.has(DataComponents.FOOD) && ServerConfigs.FOOD_COOLDOWN.get() > 0) {
                player.getCooldowns().addCooldown(stack, (int) (ServerConfigs.FOOD_COOLDOWN.get() * 20));
            }
        }
    }
}
