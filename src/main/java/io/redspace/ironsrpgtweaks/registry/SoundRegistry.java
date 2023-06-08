package io.redspace.ironsrpgtweaks.registry;

import io.redspace.ironsrpgtweaks.IronsRpgTweaks;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class SoundRegistry {
    private static final DeferredRegister<SoundEvent> SOUND_EVENTS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, IronsRpgTweaks.MODID);

    public static void register(IEventBus eventBus) {
        SOUND_EVENTS.register(eventBus);
    }

    public static RegistryObject<SoundEvent> RETRIEVE_XP = registerSoundEvent("entity.xp_catalyst.retrieve");
    public static RegistryObject<SoundEvent> IDENTIFY = registerSoundEvent("item.identification_scroll.identify");

    private static RegistryObject<SoundEvent> registerSoundEvent(String name) {
        return SOUND_EVENTS.register(name, () -> SoundEvent.m_262824_(new ResourceLocation(IronsRpgTweaks.MODID, name)));
    }
}
