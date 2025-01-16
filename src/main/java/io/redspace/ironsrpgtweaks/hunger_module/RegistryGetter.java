package io.redspace.ironsrpgtweaks.hunger_module;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;

public class RegistryGetter {


    public static Registry<Item> getItem() {
        return BuiltInRegistries.ITEM;
    }
}
