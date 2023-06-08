package io.redspace.ironsrpgtweaks.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import io.redspace.ironsrpgtweaks.IronsRpgTweaks;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.checkerframework.checker.units.qual.A;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

public class XpCatalystRenderer extends EntityRenderer<XpCatalyst> {

    private static final ResourceLocation EXPERIENCE_ORB_LOCATION = new ResourceLocation("textures/entity/experience_orb.png");
    private static final RenderType RENDER_TYPE = RenderType.itemEntityTranslucentCull(EXPERIENCE_ORB_LOCATION);

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
        float scale = 1 + Mth.sin((entity.tickCount + partialTicks) * .06f) * .05f;
        renderXpOrb(poseStack, bufferSource, entity.tickCount, partialTicks);
        poseStack.scale(scale, scale, scale);
        poseStack.scale(.6f, .6f, .6f);


        Vec3 motion = entity.getDeltaMovement();
        float xRot = -((float) (Mth.atan2(motion.horizontalDistance(), motion.y) * (double) (180F / (float) Math.PI)) - 90.0F);
        float yRot = -((float) (Mth.atan2(motion.z, motion.x) * (double) (180F / (float) Math.PI)) + 90.0F);
        //m_252781_ = mulPose
        poseStack.m_252781_(Axis.f_252436_.m_252977_(yRot));
        poseStack.m_252781_(Axis.f_252529_.m_252977_(xRot));
        VertexConsumer consumer = bufferSource.getBuffer(RenderType.entityCutoutNoCull(getTextureLocation(entity)));

        float f = entity.tickCount + partialTicks;
        float swirlX = Mth.cos(.05f * f) * 90;
        float swirlY = Mth.sin(.05f * f) * 90;
        float swirlZ = Mth.cos(.05f * f + 5464) * 90;
        poseStack.m_252781_(Axis.f_252529_.m_252977_(swirlX));
        poseStack.m_252781_(Axis.f_252436_.m_252977_(swirlY));
        poseStack.m_252781_(Axis.f_252403_.m_252977_(swirlZ));
        Vec3 green = new Vec3(.15f, 1f, .2f);
        Vec3 yellow = new Vec3(0.9f, 0.9f, .2f);
        float colorPeriod = (Mth.sin(f * .08f) + 1) * .5f;
        Vec3 color1 = green.add((yellow.subtract(green)).scale(colorPeriod));
        Vec3 color2 = green.add((yellow.subtract(green)).scale(1 - colorPeriod));
        this.orb.render(poseStack, consumer, LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY, (float) color1.x, (float) color1.y, (float) color1.z, 1f);

        poseStack.m_252781_(Axis.f_252529_.m_252977_(swirlZ));
        poseStack.m_252781_(Axis.f_252436_.m_252977_(swirlX));
        poseStack.m_252781_(Axis.f_252403_.m_252977_(swirlY));

        consumer = bufferSource.getBuffer(RenderType.entityCutoutNoCull(getSwirlTextureLocation(entity)));
        poseStack.scale(1.5f, 1.5f, 1.5f);
        this.swirl.render(poseStack, consumer, LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY, (float) color2.x, (float) color2.y, (float) color2.z, 1f);

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

    private void renderXpOrb(PoseStack pMatrixStack, MultiBufferSource pBuffer, float tickCount, float pPartialTicks) {
        pMatrixStack.pushPose();
        float scale = Mth.sin((tickCount + pPartialTicks) * .4f) * .125f + 1;
        float scale2 = Mth.cos((tickCount + pPartialTicks + 345) * .6f) * .06f + 1;
        scale *= scale2;
        pMatrixStack.scale(scale, scale, scale);
        int i = 6;
        float f = (float) (i % 4 * 16 + 0) / 64.0F;
        float f1 = (float) (i % 4 * 16 + 16) / 64.0F;
        float f2 = (float) (i / 4 * 16 + 0) / 64.0F;
        float f3 = (float) (i / 4 * 16 + 16) / 64.0F;
        float f8 = (tickCount + pPartialTicks) / 2.0F;
        int j = (int) ((Mth.sin(f8 + 0.0F) + 1.0F) * 0.5F * 255.0F);
        int l = (int) ((Mth.sin(f8 + 4.1887903F) + 1.0F) * 0.1F * 255.0F);
        pMatrixStack.translate(0.0D, (double) 0.1F, 0.0D);
        //camera orientation
        pMatrixStack.m_252781_(this.entityRenderDispatcher.m_253208_());
        //pMatrixStack.m_252781_(Axis.YP.m_252977_(180.0F));
        pMatrixStack.m_252781_(Axis.f_252436_.m_252977_(180.0F));
        float f9 = 0.3F;
        pMatrixStack.scale(0.3F, 0.3F, 0.3F);
        VertexConsumer vertexconsumer = pBuffer.getBuffer(RENDER_TYPE);
        PoseStack.Pose posestack$pose = pMatrixStack.last();
        Matrix4f matrix4f = posestack$pose.m_252922_();
        Matrix3f matrix3f = posestack$pose.m_252943_();
        vertex(vertexconsumer, matrix4f, matrix3f, -0.5F, -.75F, j, 255, l, f, f3, LightTexture.FULL_BRIGHT);
        vertex(vertexconsumer, matrix4f, matrix3f, 0.5F, -.75F, j, 255, l, f1, f3, LightTexture.FULL_BRIGHT);
        vertex(vertexconsumer, matrix4f, matrix3f, 0.5F, 0.25F, j, 255, l, f1, f2, LightTexture.FULL_BRIGHT);
        vertex(vertexconsumer, matrix4f, matrix3f, -0.5F, 0.25F, j, 255, l, f, f2, LightTexture.FULL_BRIGHT);
        pMatrixStack.popPose();
    }

    private static void vertex(VertexConsumer pBuffer, Matrix4f pMatrix, Matrix3f pMatrixNormal, float pX, float pY, int pRed, int pGreen, int pBlue, float pTexU, float pTexV, int pPackedLight) {
        pBuffer.vertex(/*pMatrix,*/ pX, pY, 0.0F).color(pRed, pGreen, pBlue, 128).uv(pTexU, pTexV).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(pPackedLight).normal(/*pMatrixNormal,*/ 0.0F, 1.0F, 0.0F).endVertex();
    }
}