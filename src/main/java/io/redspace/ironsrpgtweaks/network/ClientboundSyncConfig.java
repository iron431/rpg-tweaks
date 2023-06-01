package io.redspace.ironsrpgtweaks.network;

import io.redspace.ironsrpgtweaks.IronsRpgTweaks;
import io.redspace.ironsrpgtweaks.client.ClientConfigData;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.commands.data.DataCommands;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class ClientboundSyncConfig {

    private final boolean allowNonFullStrengthAttacks;
    private final boolean combatEnabled;
    private final float minimumAttackStrength;

    public ClientboundSyncConfig(boolean combatEnabled, boolean allowNonFullStrengthAttacks, double minimumAttackStrength) {
        this.combatEnabled = combatEnabled;
        this.allowNonFullStrengthAttacks = allowNonFullStrengthAttacks;
        this.minimumAttackStrength = (float) minimumAttackStrength;
    }

    public ClientboundSyncConfig(FriendlyByteBuf buf) {
        combatEnabled = buf.readBoolean();
        allowNonFullStrengthAttacks = buf.readBoolean();
        minimumAttackStrength = buf.readFloat();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeBoolean(combatEnabled);
        buf.writeBoolean(allowNonFullStrengthAttacks);
        buf.writeFloat(minimumAttackStrength);
    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        IronsRpgTweaks.LOGGER.debug("ClientboundSyncConfig.handle {} {} {} ", combatEnabled, allowNonFullStrengthAttacks, minimumAttackStrength);
        NetworkEvent.Context ctx = supplier.get();
        ctx.enqueueWork(() -> {
            ClientConfigData.damageModuleEnabled = this.combatEnabled;
            ClientConfigData.allowNonFullStrengthAttacks = this.allowNonFullStrengthAttacks;
            ClientConfigData.minimumAttackStrength = this.minimumAttackStrength;
        });
        return true;
    }
}
