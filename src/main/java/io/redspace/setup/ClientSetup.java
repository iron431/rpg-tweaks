package io.redspace.setup;

import io.redspace.IronsRpgTweaks;
import io.redspace.xp_module.entity.XpCatalystRenderer;
import io.redspace.registry.EntityRegistry;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

@EventBusSubscriber(modid = IronsRpgTweaks.MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientSetup {

    @SubscribeEvent
    public static void rendererRegister(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(EntityRegistry.XP_CATALYST.get(), XpCatalystRenderer::new);
    }

    @SubscribeEvent
    public static void onRegisterLayers(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(XpCatalystRenderer.MODEL_LAYER_LOCATION, XpCatalystRenderer::createBodyLayer);
    }

}
