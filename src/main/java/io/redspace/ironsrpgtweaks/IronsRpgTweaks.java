package io.redspace.ironsrpgtweaks;


import com.mojang.logging.LogUtils;
import io.redspace.ironsrpgtweaks.config.ServerConfigs;
import io.redspace.ironsrpgtweaks.registry.AttributeRegistry;
import io.redspace.ironsrpgtweaks.registry.EntityRegistry;
import io.redspace.ironsrpgtweaks.registry.PotionEffectsRegistry;
import io.redspace.ironsrpgtweaks.registry.SoundRegistry;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

@Mod(IronsRpgTweaks.MODID)
public class IronsRpgTweaks {
    // Define mod id in a common place for everything to reference
    public static final String MODID = "irons_rpg_tweaks";
    // Directly reference a slf4j logger
    public static final Logger LOGGER = LogUtils.getLogger();

    public IronsRpgTweaks() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, ServerConfigs.SPEC, String.format("%s-server.toml", IronsRpgTweaks.MODID));

        // Register the commonSetup method for modloading
        EntityRegistry.register(modEventBus);
        SoundRegistry.register(modEventBus);
        AttributeRegistry.register(modEventBus);
        PotionEffectsRegistry.register(modEventBus);

        modEventBus.addListener(this::onConfigReload);
        modEventBus.addListener(this::onConfigLoad);
        //modEventBus.addListener(CommonHungerEvents::changeStackSize);
    }

    public void onConfigReload(ModConfigEvent.Reloading event) {
        if (event.getConfig().getType() == ModConfig.Type.SERVER) {
            ServerConfigs.handleOnConfigReload();
        }
    }

    public void onConfigLoad(ModConfigEvent.Loading event) {
        if (event.getConfig().getType() == ModConfig.Type.SERVER) {
            ServerConfigs.handleOnConfigReload();
        }
    }

    public static ResourceLocation id(@NotNull String path) {
        return ResourceLocation.fromNamespaceAndPath(IronsRpgTweaks.MODID, path);
    }
}
