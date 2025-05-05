package io.redspace.ironsrpgtweaks.mixin;

import io.redspace.ironsrpgtweaks.config.ConfigHelper;
import io.redspace.ironsrpgtweaks.config.ServerConfigs;
import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LocalPlayer.class)
public abstract class LocalPlayerMixin {

    @Shadow
    public abstract boolean isUnderWater();


    @Unique
    boolean rpg_tweaks$wasUnderwater;

    @Inject(method = "updateIsUnderwater", at = @At(value = "HEAD"))
    private void rpg_tweask$captureUnderwaterValue(CallbackInfoReturnable<Boolean> cir) {
        this.rpg_tweaks$wasUnderwater = this.isUnderWater();
    }

    @Inject(method = "updateIsUnderwater", at = @At(value = "RETURN"))
    private void rpg_tweask$updateSwimSprinting(CallbackInfoReturnable<Boolean> cir) {
        if (ServerConfigs.HUNGER_MODULE_ENABLED.get()) {
            boolean nowInWater = cir.getReturnValue();
            if (!nowInWater && rpg_tweaks$wasUnderwater) {
                //exiting water
                var self = ((LocalPlayer) (Object) this);
                if (self.isSprinting() && !ServerConfigs.ALLOW_SPRINTING.get()) {
                    // even with sprinting disabled, we are allowed to "sprint" underwater (swim) but we need to stop sprinting once we emerge
                    self.setSprinting(false);
                }
            }
        }
    }

    @Inject(method = "canStartSprinting", at = @At(value = "RETURN"),cancellable = true)
    private void rpg_tweaks$handleSprintMode(CallbackInfoReturnable<Boolean> cir) {
        if (cir.getReturnValue() && ServerConfigs.HUNGER_MODULE_ENABLED.get()) {
            cir.setReturnValue(ConfigHelper.Hunger.canSprint((LocalPlayer) (Object) this));
        }
    }
}
