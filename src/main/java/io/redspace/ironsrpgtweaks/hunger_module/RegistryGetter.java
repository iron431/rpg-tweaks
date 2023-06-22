package io.redspace.ironsrpgtweaks.hunger_module;

import net.minecraft.world.item.Item;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;

public class RegistryGetter {


    public static IForgeRegistry<Item> getItem() {
        return ForgeRegistries.ITEMS;
    }
}
