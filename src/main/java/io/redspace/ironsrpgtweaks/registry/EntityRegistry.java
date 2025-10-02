package io.redspace.ironsrpgtweaks.registry;

import io.redspace.ironsrpgtweaks.IronsRpgTweaks;
import io.redspace.ironsrpgtweaks.xp_module.entity.XpCatalyst;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class EntityRegistry {
    private static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(Registries.ENTITY_TYPE, IronsRpgTweaks.MODID);

    public static void register(IEventBus eventBus) {
        ENTITIES.register(eventBus);
    }

    public static final Supplier<EntityType<XpCatalyst>> XP_CATALYST =
            ENTITIES.register("xp_catalyst", () -> EntityType.Builder.<XpCatalyst>of(XpCatalyst::new, MobCategory.MISC)
                    .sized(.6f, .6f)
                    .clientTrackingRange(64)
                    .build(ResourceLocation.fromNamespaceAndPath(IronsRpgTweaks.MODID, "xp_catalyst").toString()));

}