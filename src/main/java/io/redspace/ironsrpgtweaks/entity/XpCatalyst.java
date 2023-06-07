package io.redspace.ironsrpgtweaks.entity;

import io.redspace.ironsrpgtweaks.config.CommonConfigs;
import io.redspace.ironsrpgtweaks.registry.EntityRegistry;
import io.redspace.ironsrpgtweaks.registry.SoundRegistry;
import net.minecraft.ChatFormatting;
import net.minecraft.client.resources.sounds.Sound;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
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
    //int storedLevels;
    //int storedPoints;
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
        XpCatalyst xpCatalyst = new XpCatalyst(deadPlayer.level);
        //xpCatalyst.storedLevels = deadPlayer.experienceLevel;
        //xpCatalyst.storedPoints = (int) (deadPlayer.experienceProgress * deadPlayer.getXpNeededForNextLevel());
        xpCatalyst.storedXp = (int) (deadPlayer.experienceProgress * deadPlayer.getXpNeededForNextLevel());
        int level = deadPlayer.experienceLevel;
        for (int i = level - 1; i > 0; i--) {
            xpCatalyst.storedXp += xpCatalyst.getXpNeededForLevel(i);
        }
        xpCatalyst.ownerUUID = deadPlayer.getUUID();
        xpCatalyst.setPos(deadPlayer.position().add(0, .75, 0));
        deadPlayer.level.addFreshEntity(xpCatalyst);
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
        if (level.isClientSide)
            level.addParticle(ParticleTypes.TOTEM_OF_UNDYING, getRandomX(.125f), getRandomY(), getRandomZ(.125f), 0, 0.07, 0);
//        if (this.tickCount % 20 == 1) {
//            this.scanForEntities();
//        }
    }

    @Override
    public InteractionResult interact(Player player, InteractionHand hand) {
        //if (!player.level.isClientSide && (player.getUUID().equals(ownerUUID) || !CommonConfigs.XP_ONLY_ALLOW_OWNER.get())) {

        if (player instanceof ServerPlayer serverPlayer) {
            if (player.getUUID().equals(ownerUUID) || !CommonConfigs.XP_ONLY_ALLOW_OWNER.get()) {
                //player.giveExperienceLevels(storedLevels);
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
    public Packet<?> getAddEntityPacket() {
        return new ClientboundAddEntityPacket(this);
    }
}
