package io.redspace.ironsrpgtweaks.entity;

import io.redspace.ironsrpgtweaks.registry.EntityRegistry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.entity.EntityTypeTest;

import javax.annotation.Nullable;
import java.security.interfaces.EdECKey;
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
        if (deadPlayer.totalExperience == 0)
            return null;
        XpCatalyst xpCatalyst = new XpCatalyst(deadPlayer.level);
        xpCatalyst.storedXp = deadPlayer.totalExperience;
        xpCatalyst.ownerUUID = deadPlayer.getUUID();
        xpCatalyst.setPos(deadPlayer.position());
        deadPlayer.level.addFreshEntity(xpCatalyst);
        return xpCatalyst;
    }

    @Override
    public void tick() {
        super.tick();
//        if (this.tickCount % 20 == 1) {
//            this.scanForEntities();
//        }
    }

    @Override
    public InteractionResult interact(Player player, InteractionHand hand) {
        if (player.getUUID().equals(ownerUUID)) {
            player.giveExperiencePoints(storedXp);
            this.discard();
            return InteractionResult.SUCCESS;
        } else {
            return super.interact(player, hand);
        }
    }

    @Override
    public boolean isPickable() {
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
        storedXp = tag.getInt("StoredXp");
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        if (ownerUUID != null)
            tag.putUUID("Owner", ownerUUID);
        tag.putInt("StoredXp", storedXp);
    }

    @Override
    public Packet<?> getAddEntityPacket() {
        return new ClientboundAddEntityPacket(this);
    }
}
