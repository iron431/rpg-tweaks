package io.redspace.ironsrpgtweaks.registry;

import io.redspace.ironsrpgtweaks.IronsRpgTweaks;
import io.redspace.ironsrpgtweaks.config.ServerConfigs;
import io.redspace.ironsrpgtweaks.sleep_module.DrowsyMobEffect;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.brewing.RegisterBrewingRecipesEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

@EventBusSubscriber(modid = IronsRpgTweaks.MODID)
public class PotionEffectsRegistry {
    private static final DeferredRegister<MobEffect> MOB_EFFECTS = DeferredRegister.create(Registries.MOB_EFFECT, IronsRpgTweaks.MODID);
    private static final DeferredRegister<Potion> POTIONS = DeferredRegister.create(Registries.POTION, IronsRpgTweaks.MODID);

    public static final DeferredHolder<MobEffect, MobEffect> DROWSY_EFFECT
            = MOB_EFFECTS.register("drowsy", () -> new DrowsyMobEffect(MobEffectCategory.BENEFICIAL, 0xAAAAFF) {
        @Override
        public boolean isEnabled(FeatureFlagSet enabledFeatures) {
            return super.isEnabled(enabledFeatures) && (!ServerConfigs.SPEC.isLoaded() || ServerConfigs.SLEEP_MODULE_ENABLED.get());
        }
    });

    public static final DeferredHolder<Potion, Potion> DROWSY_POTION = POTIONS.register(
            "drowsy", () -> new Potion("drowsy", new MobEffectInstance(DROWSY_EFFECT, 20 * 60)) {
                @Override
                public boolean isEnabled(FeatureFlagSet enabledFeatures) {
                    return super.isEnabled(enabledFeatures) && (!ServerConfigs.SPEC.isLoaded() || ServerConfigs.SLEEP_MODULE_ENABLED.get());
                }
            }
    );

    @SubscribeEvent
    public static void addRecipes(RegisterBrewingRecipesEvent event) {
        // fixme: recipe still gets registered because config isn't loaded in time to prevent it, but the potion features should still be "disabled"
        event.getBuilder().addMix(Potions.AWKWARD, Items.OAK_LEAVES, DROWSY_POTION);
    }

    public static void register(IEventBus eventBus) {
        MOB_EFFECTS.register(eventBus);
        POTIONS.register(eventBus);
    }

}
