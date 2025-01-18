package io.redspace.mixin;

import io.redspace.config.ServerConfigs;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Player.class)
public class PlayerMixin {

    @Inject(method = "entityInteractionRange", at = @At(value = "RETURN"), cancellable = true)
    private void rpg_tweaks$modifyCombatReach(CallbackInfoReturnable<Double> cir) {
        if (ServerConfigs.DAMAGE_MODULE_ENABLED.get() && ServerConfigs.ENABLE_COMBAT_SNAPSHOT.get()) {
            // Scale the player's attack reach by their attack swing delay progress if we enabled combat snapshot tweaks
            cir.setReturnValue(cir.getReturnValue() * Math.max(0.2, ((Player) (Object) this).getAttackStrengthScale(0)));
        }
    }

    @ModifyArg(method = "attack", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;getAttackStrengthScale(F)F"))
    private float rpg_tweaks$disableAttackStrengthDamageScaling(float adjustTicks) {
        if (ServerConfigs.DAMAGE_MODULE_ENABLED.get() && ServerConfigs.ENABLE_COMBAT_SNAPSHOT.get()) {
            var self = (Player) (Object) this;
            // If we enabled combat snapshot tweaks, only let the attack delay scale our damage for the first 10 ticks
            // Theoretically this should be zero, but 10 provides SOME delay, preventing the combination of no iframes and no delay from becoming infinite spam damage
            if (self.attackStrengthTicker > 10) {
                return adjustTicks + self.getCurrentItemAttackStrengthDelay();
            }
        }
        return adjustTicks;
    }
}
