package io.redspace.ironsrpgtweaks.xp_module.entity;

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
import org.joml.Matrix3f;
import org.joml.Matrix4f;

public class XpCatalystRenderer extends EntityRenderer<XpCatalyst> {

    public static final ModelLayerLocation MODEL_LAYER_LOCATION = new ModelLayerLocation(IronsRpgTweaks.id("xp_catalyst_model"), "main");
    private static final ResourceLocation ORB_TEXTURE = IronsRpgTweaks.id("textures/entity/xp_catalyst/xp_catalyst_orb.png");
    private static final ResourceLocation SOLID_TEXTURE = IronsRpgTweaks.id("textures/entity/xp_catalyst/solid.png");

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

    static final Vec3 green = new Vec3(.15f, 1f, .2f);
    static final Vec3 yellow = new Vec3(0.9f, 0.9f, .2f);
    static final Vec3 white = new Vec3(1f, 1f, 1f);
    static final Vec3 purple = new Vec3(1f, .63f, 1f);

    @Override
    public void render(XpCatalyst entity, float yaw, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource, int light) {
        poseStack.pushPose();
        poseStack.translate(0, entity.getBoundingBox().getYsize() * .5f + entity.getVisualYOffset(partialTicks), 0);
        float scale = 1 + Mth.sin((entity.tickCount + partialTicks) * .06f) * .05f;
        poseStack.scale(scale, scale, scale);
        poseStack.scale(.6f, .6f, .6f);
        float f = entity.tickCount + partialTicks;
        poseStack.pushPose();
        poseStack.scale(.4f, .4f, .4f);
        float orbColorPeriod = (Mth.sin(f * .04f) + 1) * .5f;
        float colorPeriod = (Mth.sin(f * .08f) + 1) * .5f;
        float swirlX = Mth.cos(.05f * f) * 90;
        float swirlY = Mth.sin(.05f * f) * 90;
        float swirlZ = Mth.cos(.05f * f + 5464) * 90;

        Vec3 orbGradient1 = green.add((yellow.subtract(green)).scale(orbColorPeriod));
        Vec3 orbGradient2 = white.add((purple.subtract(white)).scale(orbColorPeriod));
        Vec3 orbGradient3 = orbGradient1.add((orbGradient2.subtract(orbGradient1)).scale(colorPeriod));
        poseStack.mulPose(Axis.XP.rotationDegrees(swirlX * .45f));
        poseStack.mulPose(Axis.YP.rotationDegrees(swirlY * .45f));
        poseStack.mulPose(Axis.ZP.rotationDegrees(swirlZ * .45f));
        VertexConsumer consumer = bufferSource.getBuffer(RenderType.entityCutoutNoCull(SOLID_TEXTURE));
        this.orb.render(poseStack, consumer, LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY, colorf((float) orbGradient3.x, (float) orbGradient3.y, (float) orbGradient3.z, 1f));
        poseStack.popPose();


        consumer = bufferSource.getBuffer(RenderType.entityCutoutNoCull(getTextureLocation(entity)));

        poseStack.mulPose(Axis.XP.rotationDegrees(swirlX));
        poseStack.mulPose(Axis.YP.rotationDegrees(swirlY));
        poseStack.mulPose(Axis.ZP.rotationDegrees(swirlZ));

        Vec3 rimGradient = green.add((yellow.subtract(green)).scale(colorPeriod));
        Vec3 rimGradientInverted = green.add((yellow.subtract(green)).scale(1 - colorPeriod));
        this.orb.render(poseStack, consumer, LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY, colorf((float) rimGradient.x, (float) rimGradient.y, (float) rimGradient.z, 1f));

        poseStack.mulPose(Axis.XP.rotationDegrees(swirlZ));
        poseStack.mulPose(Axis.YP.rotationDegrees(swirlX));
        poseStack.mulPose(Axis.ZP.rotationDegrees(swirlY));

        poseStack.scale(1.5f, 1.5f, 1.5f);
        this.swirl.render(poseStack, consumer, LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY, colorf((float) rimGradientInverted.x, (float) rimGradientInverted.y, (float) rimGradientInverted.z, 1f));

        poseStack.popPose();

        super.render(entity, yaw, partialTicks, poseStack, bufferSource, light);
    }

    @Override
    public ResourceLocation getTextureLocation(XpCatalyst entity) {
        return ORB_TEXTURE;
    }

    private static void vertex(VertexConsumer pBuffer, Matrix4f pMatrix, Matrix3f pMatrixNormal, float pX, float pY, int pRed, int pGreen, int pBlue, float pTexU, float pTexV, int pPackedLight) {
        pBuffer.addVertex(/*pMatrix,*/ pX, pY, 0.0F).setColor(pRed, pGreen, pBlue, 128).setUv(pTexU, pTexV).setOverlay(OverlayTexture.NO_OVERLAY).setLight(pPackedLight).setNormal(/*pMatrixNormal,*/ 0.0F, 1.0F, 0.0F);
    }

    public static int color255(int pRed, int pGreen, int pBlue, int pAlpha) {
        return pAlpha << 24 | pRed << 16 | pGreen << 8 | pBlue;
    }

    public static int color255(int pRed, int pGreen, int pBlue) {
        return color255(pRed, pGreen, pBlue, 255);
    }

    public static int colorf(float pRed, float pGreen, float pBlue, float pAlpha) {
        return color255((int) (255 * pRed), (int) (255 * pGreen), (int) (255 * pBlue), (int) (255 * pAlpha));
    }

    public static int colorf(float pRed, float pGreen, float pBlue) {
        return colorf(pRed, pGreen, pBlue, 1f);
    }
}