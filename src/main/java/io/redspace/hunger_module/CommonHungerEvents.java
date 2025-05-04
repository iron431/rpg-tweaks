package io.redspace.hunger_module;

import io.redspace.config.ConfigHelper;
import io.redspace.config.ServerConfigs;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.ModifyDefaultComponentsEvent;
import net.neoforged.neoforge.event.entity.living.LivingEntityUseItemEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

@EventBusSubscriber
public class CommonHungerEvents {

    /**
     * manually call event via on config reload
     */
    public static void modifyDefaultStackSize(BiConsumer<Item, Consumer<DataComponentPatch.Builder>> modifyCallback) {
        if (!ServerConfigs.HUNGER_MODULE_ENABLED.get()) {
            return;
        }
        int food = ServerConfigs.FOOD_STACK_SIZE.get();
        int potions = ServerConfigs.POTION_STACK_SIZE.get();
        if (potions > 1) {
            modifyCallback.accept(Items.POTION, builder -> builder.set(DataComponents.MAX_STACK_SIZE, Math.min(potions, 99)));
            modifyCallback.accept(Items.SPLASH_POTION, builder -> builder.set(DataComponents.MAX_STACK_SIZE, Math.min(potions, 99)));
            modifyCallback.accept(Items.LINGERING_POTION, builder -> builder.set(DataComponents.MAX_STACK_SIZE, Math.min(potions, 99)));
        }
        if (food > 0) {
            for (Item item : BuiltInRegistries.ITEM) {
                var fooddata = item.components().get(DataComponents.FOOD);
                if (fooddata != null) {
                    if (fooddata.nutrition() >= 5 || fooddata.effects().stream().anyMatch(possibleEffect -> possibleEffect.effect().getEffect().value().isBeneficial())) {
                        modifyCallback.accept(item, builder -> builder.set(DataComponents.MAX_STACK_SIZE, Math.min(food, Math.min(item.getDefaultMaxStackSize(), 99))));
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
            player.getCooldowns().addCooldown(Items.SPLASH_POTION, (int) (ServerConfigs.SPLASH_POTION_COOLDOWN.get() * 20));
        } else if (event.getItemStack().is(Items.LINGERING_POTION) && ServerConfigs.LINGERING_POTION_COOLDOWN.get() > 0) {
            player.getCooldowns().addCooldown(Items.LINGERING_POTION, (int) (ServerConfigs.LINGERING_POTION_COOLDOWN.get() * 20));
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
                player.getCooldowns().addCooldown(Items.POTION, (int) (ServerConfigs.DRINKABLE_POTION_COOLDOWN.get() * 20));
            }
            if (stack.has(DataComponents.FOOD) && ServerConfigs.FOOD_COOLDOWN.get() > 0) {
                player.getCooldowns().addCooldown(stack.getItem(), (int) (ServerConfigs.FOOD_COOLDOWN.get() * 20));
            }
        }
    }
}
