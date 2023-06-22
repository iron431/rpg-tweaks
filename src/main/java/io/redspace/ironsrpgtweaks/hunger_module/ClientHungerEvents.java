package io.redspace.ironsrpgtweaks.hunger_module;

import io.redspace.ironsrpgtweaks.config.ServerConfigs;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(Dist.CLIENT)
public class ClientHungerEvents {

    @SubscribeEvent
    public static void disableHunger(RenderGuiOverlayEvent.Pre event) {
        if (event.getOverlay() == VanillaGuiOverlay.FOOD_LEVEL.type() && ServerConfigs.HUNGER_MODULE_ENABLED.get())
            event.setCanceled(true);
    }
}
