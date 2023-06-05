package io.redspace.ironsrpgtweaks.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix3f;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import io.redspace.ironsrpgtweaks.IronsRpgTweaks;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

public class XpCatalystRenderer extends EntityRenderer<XpCatalyst> {

    public static final ModelLayerLocation MODEL_LAYER_LOCATION = new ModelLayerLocation(new ResourceLocation(IronsRpgTweaks.MODID, "xp_catalyst_model"), "main");
    private static ResourceLocation ORB_TEXTURE = IronsRpgTweaks.id("textures/entity/xp_catalyst/xp_catalyst_orb.png");
    private static ResourceLocation SWIRL_TEXTURES[] = {
            IronsRpgTweaks.id("textures/entity/xp_catalyst/swirl_0.png"),
            IronsRpgTweaks.id("textures/entity/xp_catalyst/swirl_1.png"),
            IronsRpgTweaks.id("textures/entity/xp_catalyst/swirl_2.png"),
            IronsRpgTweaks.id("textures/entity/xp_catalyst/swirl_3.png"),
            IronsRpgTweaks.id("textures/entity/xp_catalyst/swirl_4.png"),
            IronsRpgTweaks.id("textures/entity/xp_catalyst/swirl_5.png"),
            IronsRpgTweaks.id("textures/entity/xp_catalyst/swirl_6.png"),
            IronsRpgTweaks.id("textures/entity/xp_catalyst/swirl_7.png"),
            IronsRpgTweaks.id("textures/entity/xp_catalyst/swirl_8.png"),
            IronsRpgTweaks.id("textures/entity/xp_catalyst/swirl_9.png"),
            IronsRpgTweaks.id("textures/entity/xp_catalyst/swirl_10.png")
    };

    private final ModelPart orb;
    private final ModelPart swirl;
    private final ModelPart swirl2;

    public XpCatalystRenderer(EntityRendererProvider.Context context) {
        super(context);
        ModelPart modelpart = context.bakeLayer(MODEL_LAYER_LOCATION);
        this.orb = modelpart.getChild("orb");
        this.swirl = modelpart.getChild("swirl");
        this.swirl2 = modelpart.getChild("swirl2");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();
        partdefinition.addOrReplaceChild("orb", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -4.0F, -4.0F, 8.0F, 8.0F, 8.0F), PartPose.ZERO);
        partdefinition.addOrReplaceChild("swirl", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -4.0F, -4.0F, 8.0F, 8.0F, 8.0F), PartPose.ZERO);
        partdefinition.addOrReplaceChild("swirl2", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -4.0F, -4.0F, 8.0F, 8.0F, 8.0F), PartPose.ZERO);
        return LayerDefinition.create(meshdefinition, 8, 8);
    }

    @Override
    public void render(XpCatalyst entity, float yaw, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource, int light) {
        poseStack.pushPose();
        poseStack.translate(0, entity.getBoundingBox().getYsize() * .5f + entity.getVisualYOffset(partialTicks), 0);
        poseStack.scale(1.25f, 1.25f, 1.25f);
        float scale = 1 + Mth.sin((entity.tickCount + partialTicks) * .12f) * .125f;
        poseStack.scale(scale, scale, scale);

        PoseStack.Pose pose = poseStack.last();
        Matrix4f poseMatrix = pose.pose();
        Matrix3f normalMatrix = pose.normal();
        Vec3 motion = entity.getDeltaMovement();
        float xRot = -((float) (Mth.atan2(motion.horizontalDistance(), motion.y) * (double) (180F / (float) Math.PI)) - 90.0F);
        float yRot = -((float) (Mth.atan2(motion.z, motion.x) * (double) (180F / (float) Math.PI)) + 90.0F);
        poseStack.mulPose(Vector3f.YP.rotationDegrees(yRot));
        poseStack.mulPose(Vector3f.XP.rotationDegrees(xRot));
        VertexConsumer consumer = bufferSource.getBuffer(RenderType.entityCutoutNoCull(getTextureLocation(entity)));

        float f = entity.tickCount + partialTicks;
        float swirlX = Mth.cos(.07f * f) * 90;
        float swirlY = Mth.sin(.07f * f) * 90;
        float swirlZ = Mth.cos(.07f * f + 5464) * 90;
        poseStack.mulPose(Vector3f.XP.rotationDegrees(swirlX));
        poseStack.mulPose(Vector3f.YP.rotationDegrees(swirlY));
        poseStack.mulPose(Vector3f.ZP.rotationDegrees(swirlZ));
        Vec3 green = new Vec3(.15f, 1f, .2f);
        Vec3 yellow = new Vec3(.5f, 1f, .2f);
        float colorPeriod = (Mth.sin(f * .15f) + 1) * .5f;
        Vec3 color1 = green.add((yellow.subtract(green)).scale(colorPeriod));
        Vec3 color2 = green.add((yellow.subtract(green)).scale(1 - colorPeriod));
        this.orb.render(poseStack, consumer, light, OverlayTexture.NO_OVERLAY, (float) color1.x, (float) color1.y, (float) color1.z, 1f);

        poseStack.mulPose(Vector3f.XP.rotationDegrees(swirlZ));
        poseStack.mulPose(Vector3f.YP.rotationDegrees(swirlX));
        poseStack.mulPose(Vector3f.ZP.rotationDegrees(swirlY));

        consumer = bufferSource.getBuffer(RenderType.entityCutoutNoCull(getSwirlTextureLocation(entity)));
        poseStack.scale(1.25f, 1.25f, 1.25f);
        this.swirl.render(poseStack, consumer, light, OverlayTexture.NO_OVERLAY, (float) color2.x, (float) color2.y, (float) color2.z, 1f);

        poseStack.mulPose(Vector3f.XP.rotationDegrees(swirlX * -2.5345f));
        poseStack.mulPose(Vector3f.YP.rotationDegrees(swirlY * -3.5345f));
        poseStack.mulPose(Vector3f.ZP.rotationDegrees(swirlZ * -2.5345f));

        //this.swirl2.render(poseStack, consumer, light, OverlayTexture.NO_OVERLAY);

        poseStack.popPose();

        super.render(entity, yaw, partialTicks, poseStack, bufferSource, light);
    }

    @Override
    public ResourceLocation getTextureLocation(XpCatalyst entity) {
        return ORB_TEXTURE;
    }

    private ResourceLocation getSwirlTextureLocation(XpCatalyst entity) {
        if (true)
            return getTextureLocation(entity);
        int frame = (entity.tickCount / 2) % SWIRL_TEXTURES.length;
        return SWIRL_TEXTURES[frame];
    }
}