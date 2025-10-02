package io.redspace.ironsrpgtweaks.registry;

import io.redspace.ironsrpgtweaks.IronsRpgTweaks;
import io.redspace.ironsrpgtweaks.sleep_module.DrowsyMobEffect;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionBrewing;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Supplier;

@Mod.EventBusSubscriber
public class PotionEffectsRegistry {
    private static final DeferredRegister<MobEffect> MOB_EFFECTS = DeferredRegister.create(Registries.MOB_EFFECT, IronsRpgTweaks.MODID);
    private static final DeferredRegister<Potion> POTIONS = DeferredRegister.create(Registries.POTION, IronsRpgTweaks.MODID);

    public static final Supplier<MobEffect> DROWSY_EFFECT
            = MOB_EFFECTS.register("drowsy", () -> new DrowsyMobEffect(MobEffectCategory.BENEFICIAL, 0xAAAAFF) /*{
        @Override
        public boolean isEnabled(FeatureFlagSet enabledFeatures) {
            return super.isEnabled(enabledFeatures) && ServerConfigs.SLEEP_MODULE_ENABLED.get();
        }
    }*/);

    public static final Supplier<Potion> DROWSY_POTION = POTIONS.register(
            "drowsy", () -> new Potion(new MobEffectInstance(DROWSY_EFFECT.get(), 20 * 60))/* {
                @Override
                public boolean isEnabled(FeatureFlagSet enabledFeatures) {
                    return super.isEnabled(enabledFeatures) && ServerConfigs.SLEEP_MODULE_ENABLED.get();
                }
            }*/
    );

//    @SubscribeEvent
//    public static void addRecipes(Brewev event) {
//        ItemStack awkward = new ItemStack(Items.POTION);
//        awkward.set(DataComponents.POTION_CONTENTS, new PotionContents(Potions.AWKWARD));
//        ItemStack potion = new ItemStack(Items.POTION);
//        potion.set(DataComponents.POTION_CONTENTS, new PotionContents(DROWSY_POTION));
//        event.getBuilder().addRecipe(Ingredient.of(awkward), Ingredient.of(ItemTags.LEAVES), potion);
//    }

    public static void addRecipes(FMLCommonSetupEvent event) {
        //IronsSpellbooks.LOGGER.debug("adding potion recipes");

        event.enqueueWork(() -> {
            PotionBrewing.POTION_MIXES.add(new PotionBrewing.Mix<>(ForgeRegistries.POTIONS, Potions.AWKWARD, Ingredient.of(ItemTags.LEAVES), DROWSY_POTION.get()));
        });
    }

    public static void register(IEventBus eventBus) {
        MOB_EFFECTS.register(eventBus);
        POTIONS.register(eventBus);
        eventBus.addListener(PotionEffectsRegistry::addRecipes);
    }

}
