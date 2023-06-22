package io.redspace.ironsrpgtweaks.hunger_module;

import net.minecraft.world.item.Item;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Consumer;

public class RegistryGetter {

    public static void iterateItems(Consumer<Item> action) {
        ForgeRegistries.ITEMS.forEach(action);
    }
}
