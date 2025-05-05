package io.redspace.ironsrpgtweaks.mixin;

import io.redspace.ironsrpgtweaks.config.ServerConfigs;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(Gui.class)
public class GuiMixin {

    @Shadow
    Minecraft minecraft;

    @ModifyArg(method = "renderCrosshair", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;getAttackStrengthScale(F)F"))
    private float rpg_tweaks$disableCrosshairIndicator(float adjustTicks) {
        if (ServerConfigs.DAMAGE_MODULE_ENABLED.get() && ServerConfigs.ENABLE_COMBAT_SNAPSHOT.get()) {
            // If combat snapshot tweaks enabled, don't render progress meter on crosshair (skip progress to 100% always)
            return adjustTicks + minecraft.player.getCurrentItemAttackStrengthDelay();
        }
        return adjustTicks;
    }
}
