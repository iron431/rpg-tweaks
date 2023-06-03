package io.redspace.ironsrpgtweaks.registry;

import com.mojang.serialization.Codec;
import io.redspace.ironsrpgtweaks.IronsRpgTweaks;
import io.redspace.ironsrpgtweaks.enchantment_module.HideEnchantsLootModifier;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class LootRegistry {
    public static final DeferredRegister<Codec<? extends IGlobalLootModifier>> LOOT_MODIFIER_SERIALIZERS = DeferredRegister.create(ForgeRegistries.Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS, IronsRpgTweaks.MODID);

    public static void register(IEventBus eventBus) {
        LOOT_MODIFIER_SERIALIZERS.register(eventBus);
        IronsRpgTweaks.LOGGER.debug("Registering loot modifiers");
    }

    public static final RegistryObject<Codec<? extends IGlobalLootModifier>> HIDE_ENCHANTS_MODIFIER = LOOT_MODIFIER_SERIALIZERS.register("hide_enchants", HideEnchantsLootModifier.CODEC);

}
