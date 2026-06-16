package io.redspace.ironsrpgtweaks.xp_module.entity;

import net.minecraft.core.UUIDUtil;
import io.redspace.ironsrpgtweaks.config.ServerConfigs;
import io.redspace.ironsrpgtweaks.registry.EntityRegistry;
import io.redspace.ironsrpgtweaks.registry.SoundRegistry;
import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundSetActionBarTextPacket;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.UUID;

public class XpCatalyst extends Entity {
    UUID ownerUUID;
    int storedXp;

    public XpCatalyst(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {

    }

    public XpCatalyst(Level level) {
        this(EntityRegistry.XP_CATALYST.get(), level);

    }

    @Nullable
    public static XpCatalyst createXpCatalyst(ServerPlayer deadPlayer) {
        if (deadPlayer.experienceLevel == 0 && deadPlayer.experienceProgress == 0)
            return null;
        XpCatalyst xpCatalyst = new XpCatalyst(deadPlayer.level());
        xpCatalyst.storedXp = (int) (deadPlayer.experienceProgress * deadPlayer.getXpNeededForNextLevel());
        int level = deadPlayer.experienceLevel;
        for (int i = level - 1; i >= 0; i--) {
            xpCatalyst.storedXp += xpCatalyst.getXpNeededForLevel(i);
        }
        xpCatalyst.ownerUUID = deadPlayer.getUUID();
        xpCatalyst.setPos(deadPlayer.position().add(0, .75, 0));
        deadPlayer.level().addFreshEntity(xpCatalyst);
        return xpCatalyst;
    }

    public int getXpNeededForLevel(int level) {
        if (level >= 30) {
            return 112 + (level - 30) * 9;
        } else {
            return level >= 15 ? 37 + (level - 15) * 5 : 7 + level * 2;
        }
    }

    public float getVisualYOffset(float partialTick) {
        return Mth.sin((tickCount + partialTick) * .04f) * .25f;
    }

    @Override
    public void tick() {
        if (level().isClientSide()) {
            level().addParticle(ParticleTypes.TOTEM_OF_UNDYING, getRandomX(.125f), getRandomY(), getRandomZ(.125f), 0, 0.07, 0);
        }
        if (firstTick) {
            firstTick = false;
        }
        this.checkBelowWorld();
    }

    @Override
    public boolean hurtServer(ServerLevel level, DamageSource source, float damage) {
        return false;
    }

    @Override
    public boolean canBeHitByProjectile() {
        return false;
    }

    @Override
    public boolean isPickable() {
        return true;
    }

    @Override
    public @NotNull InteractionResult interact(Player player, InteractionHand hand, Vec3 location) {
        if (player instanceof ServerPlayer serverPlayer) {
            if (player.getUUID().equals(ownerUUID) || !ServerConfigs.XP_ONLY_ALLOW_OWNER.get()) {
                player.giveExperiencePoints(storedXp);
                this.playSound(SoundRegistry.RETRIEVE_XP.get());
                this.discard();
                return InteractionResult.SUCCESS;
            } else {
                serverPlayer.connection.send(new ClientboundSetActionBarTextPacket(Component.translatable("ui.irons_rpg_tweaks.xp_retrieve_error").withStyle(ChatFormatting.RED)));
            }
        }
        return super.interact(player, hand, location);
    }

    @Override
    public boolean shouldBeSaved() {
        return true;
    }

    @Override
    protected void readAdditionalSaveData(ValueInput input) {
        ownerUUID = input.read("Owner", UUIDUtil.CODEC).orElse(null);
        storedXp = input.getIntOr("StoredXp", 0);
    }

    @Override
    protected void addAdditionalSaveData(ValueOutput output) {
        if (ownerUUID != null) {
            output.store("Owner", UUIDUtil.CODEC, ownerUUID);
        }
        output.putInt("StoredXp", storedXp);
    }
}
