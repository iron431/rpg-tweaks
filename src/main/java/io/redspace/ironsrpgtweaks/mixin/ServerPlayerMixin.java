package io.redspace.ironsrpgtweaks.mixin;

import com.mojang.datafixers.util.Either;
import io.redspace.ironsrpgtweaks.config.ServerConfigs;
import io.redspace.ironsrpgtweaks.registry.PotionEffectsRegistry;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Unit;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.eventbus.api.Event;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * 1.20.1 only due to limited event access
 */
@Mixin(Player.class)
public class ServerPlayerMixin {
    @Inject(method = "startSleepInBed", at = @At(value = "HEAD"), cancellable = true)
    public void rpg_tweaks$preventSleep(BlockPos pAt, CallbackInfoReturnable<Either<Player.BedSleepingProblem, Unit>> cir) {
        if (!ServerConfigs.SLEEP_MODULE_ENABLED.get()) {
            return;
        }
        var entity = (Player) (Object) this;
        if (!entity.hasEffect(PotionEffectsRegistry.DROWSY_EFFECT.get()) && !entity.isCreative()) {
            cir.setReturnValue(Either.left(Player.BedSleepingProblem.OTHER_PROBLEM));
            entity.displayClientMessage(Component.translatable("ui.irons_rpg_tweaks.sleep_failure").withStyle(ChatFormatting.RED), true);
        }
    }
}
