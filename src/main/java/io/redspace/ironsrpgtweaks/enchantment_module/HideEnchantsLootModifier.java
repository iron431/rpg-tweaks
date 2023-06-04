package io.redspace.ironsrpgtweaks.enchantment_module;

import com.google.common.base.Suppliers;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.redspace.ironsrpgtweaks.IronsRpgTweaks;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.common.loot.LootModifier;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public class HideEnchantsLootModifier extends LootModifier {
    public static final Supplier<Codec<HideEnchantsLootModifier>> CODEC = Suppliers.memoize(() -> RecordCodecBuilder.create(inst -> codecStart(inst).apply(inst, HideEnchantsLootModifier::new)));

    protected HideEnchantsLootModifier(LootItemCondition[] conditionsIn) {
        super(conditionsIn);
    }

    @Override
    protected @NotNull ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
        //TODO: blacklist loot tables config
        //IronsRpgTweaks.LOGGER.debug("HideEnchantsLootModifier: RUNNING");
        for (ItemStack itemStack : generatedLoot) {
            var enchants = EnchantHelper.getEnchantments(itemStack);
            if (enchants != null){
                EnchantHelper.hideEnchantments(itemStack);
            }
        }
        return generatedLoot;
    }

    @Override
    public Codec<? extends IGlobalLootModifier> codec() {
        //IronsRpgTweaks.LOGGER.debug("HideEnchantsLootModifier: accessing codec");
        return CODEC.get();
    }
}
