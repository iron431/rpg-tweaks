package io.redspace.hunger_module;

import io.redspace.config.ConfigHelper;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.CustomizeGuiOverlayEvent;
import net.neoforged.neoforge.client.event.RenderGuiLayerEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;

@EventBusSubscriber(Dist.CLIENT)
public class ClientHungerEvents {

    @SubscribeEvent
    public static void disableHunger(RenderGuiLayerEvent.Pre event) {
        if (event.getName().equals(VanillaGuiLayers.FOOD_LEVEL) && ConfigHelper.Hunger.shouldDisableVanillaHunger())
            event.setCanceled(true);
    }
}
