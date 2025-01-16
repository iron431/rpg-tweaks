package io.redspace.ironsrpgtweaks.registry;

import io.redspace.ironsrpgtweaks.IronsRpgTweaks;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityAttributeModificationEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

@EventBusSubscriber(modid = IronsRpgTweaks.MODID, bus = EventBusSubscriber.Bus.MOD)
public class AttributeRegistry {

    private static final DeferredRegister<Attribute> ATTRIBUTES = DeferredRegister.create(Registries.ATTRIBUTE, IronsRpgTweaks.MODID);

    public static void register(IEventBus eventBus) {
        ATTRIBUTES.register(eventBus);
    }

    public static final DeferredHolder<Attribute, Attribute> NATURAL_REGEN_SPEED = ATTRIBUTES.register("natural_regen_speed", () -> (new RangedAttribute("attribute." + IronsRpgTweaks.MODID + ".natural_regen_speed", 1.0D, 0.0D, 1000.0D).setSyncable(false)));
    public static final DeferredHolder<Attribute, Attribute> NATURAL_REGEN_AMOUNT = ATTRIBUTES.register("natural_regen_amount", () -> (new RangedAttribute("attribute." + IronsRpgTweaks.MODID + ".natural_regen_amount", 1.0D, 0.0D, 1000.0D).setSyncable(false)));

    @SubscribeEvent
    public static void modifyEntityAttributes(EntityAttributeModificationEvent e) {
        e.getTypes().forEach(entity -> ATTRIBUTES.getEntries().forEach(attribute -> e.add(entity, attribute)));
    }
}
