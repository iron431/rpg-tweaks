package io.redspace.ironsrpgtweaks.registry;

import io.redspace.ironsrpgtweaks.IronsRpgTweaks;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.minecraftforge.event.entity.EntityAttributeModificationEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@EventBusSubscriber(modid = IronsRpgTweaks.MODID, bus = EventBusSubscriber.Bus.MOD)
public class AttributeRegistry {

    private static final DeferredRegister<Attribute> ATTRIBUTES = DeferredRegister.create(ForgeRegistries.ATTRIBUTES, IronsRpgTweaks.MODID);

    public static void register(IEventBus eventBus) {
        ATTRIBUTES.register(eventBus);
    }
    
    public static final RegistryObject<Attribute> NATURAL_REGEN_SPEED = ATTRIBUTES.register("natural_regen_speed", () -> (new RangedAttribute("attribute." + IronsRpgTweaks.MODID + ".natural_regen_speed", 1.0D, 0.0D, 1000.0D).setSyncable(false)));
    public static final RegistryObject<Attribute> NATURAL_REGEN_AMOUNT = ATTRIBUTES.register("natural_regen_amount", () -> (new RangedAttribute("attribute." + IronsRpgTweaks.MODID + ".natural_regen_amount", 1.0D, 0.0D, 1000.0D).setSyncable(false)));

    @SubscribeEvent
    public static void modifyEntityAttributes(EntityAttributeModificationEvent e) {
        e.getTypes().forEach(entity -> ATTRIBUTES.getEntries().forEach(attribute -> e.add(entity, attribute.get())));
    }
}
