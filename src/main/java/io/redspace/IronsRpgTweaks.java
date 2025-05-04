package io.redspace;


import com.mojang.logging.LogUtils;
import io.redspace.config.ServerConfigs;
import io.redspace.registry.AttributeRegistry;
import io.redspace.registry.EntityRegistry;
import io.redspace.registry.PotionEffectsRegistry;
import io.redspace.registry.SoundRegistry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.flag.FeatureFlag;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.flag.FeatureFlags;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.util.flag.FeatureFlagLoader;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

@Mod(IronsRpgTweaks.MODID)
public class IronsRpgTweaks {
    public static final String MODID = "irons_rpg_tweaks";
    public static final Logger LOGGER = LogUtils.getLogger();

    public IronsRpgTweaks(IEventBus modEventBus, ModContainer modContainer) {
        modContainer.registerConfig(ModConfig.Type.SERVER, ServerConfigs.SPEC, String.format("%s-server.toml", IronsRpgTweaks.MODID));

        // Register the commonSetup method for modloading
        EntityRegistry.register(modEventBus);
        SoundRegistry.register(modEventBus);
        AttributeRegistry.register(modEventBus);
        PotionEffectsRegistry.register(modEventBus);

        modEventBus.addListener(this::onConfigReload);
        modEventBus.addListener(this::onConfigLoad);
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
