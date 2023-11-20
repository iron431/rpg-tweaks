package io.redspace.ironsrpgtweaks.entity;

import io.redspace.ironsrpgtweaks.IronsRpgTweaks;
import io.redspace.ironsrpgtweaks.config.ServerConfigs;
import io.redspace.ironsrpgtweaks.registry.EntityRegistry;
import io.redspace.ironsrpgtweaks.registry.SoundRegistry;
import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.protocol.game.ClientboundSetActionBarTextPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.fluids.FluidType;

import javax.annotation.Nullable;
import java.util.UUID;

public class XpCatalyst extends Entity {
    UUID ownerUUID;
    int storedXp;

    public XpCatalyst(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    public XpCatalyst(Level level) {
        this(EntityRegistry.XP_CATALYST.get(), level);

    }

    @Nullable
    public static XpCatalyst createXpCatalyst(ServerPlayer deadPlayer) {
        if (deadPlayer.experienceLevel == 0 && deadPlayer.experienceProgress == 0)
            return null;
        XpCatalyst xpCatalyst = new XpCatalyst(deadPlayer.getLevel());
        //xpCatalyst.storedLevels = deadPlayer.experienceLevel;
        //xpCatalyst.storedPoints = (int) (deadPlayer.experienceProgress * deadPlayer.getXpNeededForNextLevel());
        xpCatalyst.storedXp = (int) (deadPlayer.experienceProgress * deadPlayer.getXpNeededForNextLevel());
        int level = deadPlayer.experienceLevel;
        for (int i = level - 1; i >= 0; i--) {
            xpCatalyst.storedXp += xpCatalyst.getXpNeededForLevel(i);
        }
        xpCatalyst.ownerUUID = deadPlayer.getUUID();
        xpCatalyst.setPos(deadPlayer.position().add(0, .75, 0));
        deadPlayer.getLevel().addFreshEntity(xpCatalyst);
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
        super.tick();
        if (getLevel().isClientSide) {
            getLevel().addParticle(ParticleTypes.TOTEM_OF_UNDYING, getRandomX(.125f), getRandomY(), getRandomZ(.125f), 0, 0.07, 0);
        }
    }

    @Override
    public InteractionResult interact(Player player, InteractionHand hand) {
        if (player instanceof ServerPlayer serverPlayer) {
//            if(serverPlayer.isCrouching()){
//                String level = "Level: " + serverPlayer.experienceLevel;
//                String progress = "Progress: " + serverPlayer.experienceProgress;
//                String nextLevel = "Xp Needed for next Level: " + serverPlayer.getXpNeededForNextLevel();
//                String point = "Point Estimate: " + serverPlayer.getXpNeededForNextLevel() * serverPlayer.experienceProgress;
//                IronsRpgTweaks.LOGGER.debug(level);
//                IronsRpgTweaks.LOGGER.debug(progress);
//                IronsRpgTweaks.LOGGER.debug(nextLevel);
//                IronsRpgTweaks.LOGGER.debug(point);
//                serverPlayer.sendSystemMessage(Component.literal(level));
//                serverPlayer.sendSystemMessage(Component.literal(progress));
//                serverPlayer.sendSystemMessage(Component.literal(nextLevel));
//                serverPlayer.sendSystemMessage(Component.literal(point));
//                return InteractionResult.SUCCESS;
//            }
            if (player.getUUID().equals(ownerUUID) || !ServerConfigs.XP_ONLY_ALLOW_OWNER.get()) {
                player.giveExperiencePoints(storedXp);
                this.playSound(SoundRegistry.RETRIEVE_XP.get());
                this.discard();
                return InteractionResult.SUCCESS;
            } else {
                serverPlayer.connection.send(new ClientboundSetActionBarTextPacket(Component.translatable("ui.irons_rpg_tweaks.xp_retrieve_error").withStyle(ChatFormatting.RED)));
            }
        }
        return super.interact(player, hand);

    }

    @Override
    public boolean isAlive() {
        return false;
    }

    @Override
    public boolean isPushedByFluid(FluidType type) {
        return false;
    }

    @Override
    public boolean isPickable() {
        return true;
    }

    @Override
    public boolean shouldBeSaved() {
        return true;
    }

//
//    private void scanForEntities() {
//        if (this.followingPlayer == null || this.followingPlayer.distanceToSqr(this) > 64.0D) {
//            this.followingPlayer = this.level.getNearestPlayer(this, 8.0D);
//        }
//
//        if (this.level instanceof ServerLevel) {
//            for(ExperienceOrb experienceorb : this.level.getEntities(EntityTypeTest.forClass(ExperienceOrb.class), this.getBoundingBox().inflate(0.5D), this::canMerge)) {
//                this.merge(experienceorb);
//            }
//        }
//
//    }

    @Override
    protected void defineSynchedData() {

    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        if (tag.hasUUID("Owner")) {
            ownerUUID = tag.getUUID("Owner");
        }
        //storedLevels = tag.getInt("StoredLevels");
        //storedPoints = tag.getInt("StoredPoints");
        storedXp = tag.getInt("StoredXp");
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        if (ownerUUID != null)
            tag.putUUID("Owner", ownerUUID);
        //tag.putInt("StoredLevels", storedLevels);
        //tag.putInt("StoredPoints", storedPoints);
        tag.putInt("StoredXp", storedXp);
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return new ClientboundAddEntityPacket(this);
    }
}
