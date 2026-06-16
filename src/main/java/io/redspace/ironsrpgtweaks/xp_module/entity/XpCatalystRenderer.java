package io.redspace.ironsrpgtweaks.xp_module.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import io.redspace.ironsrpgtweaks.IronsRpgTweaks;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

public class XpCatalystRenderer extends EntityRenderer<XpCatalyst, XpCatalystRenderState> {

    private static final int FULL_BRIGHT = 15728880;

    public static final ModelLayerLocation MODEL_LAYER_LOCATION = new ModelLayerLocation(IronsRpgTweaks.id("xp_catalyst_model"), "main");
    private static final Identifier ORB_TEXTURE = IronsRpgTweaks.id("textures/entity/xp_catalyst/xp_catalyst_orb.png");
    private static final Identifier SOLID_TEXTURE = IronsRpgTweaks.id("textures/entity/xp_catalyst/solid.png");

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
    public XpCatalystRenderState createRenderState() {
        return new XpCatalystRenderState();
    }

    @Override
    public void extractRenderState(XpCatalyst entity, XpCatalystRenderState state, float partialTicks) {
        super.extractRenderState(entity, state, partialTicks);
        state.visualYOffset = entity.getVisualYOffset(partialTicks);
        state.scale = 1 + Mth.sin((entity.tickCount + partialTicks) * .06f) * .05f;
        state.age = entity.tickCount + partialTicks;
        state.orbColorPeriod = (Mth.sin(state.age * .04f) + 1) * .5f;
        state.colorPeriod = (Mth.sin(state.age * .08f) + 1) * .5f;
        state.swirlX = Mth.cos(.05f * state.age) * 90;
        state.swirlY = Mth.sin(.05f * state.age) * 90;
        state.swirlZ = Mth.cos(.05f * state.age + 5464) * 90;
    }

    @Override
    public void submit(XpCatalystRenderState state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState camera) {
        poseStack.pushPose();
        poseStack.translate(0, state.boundingBoxHeight * .5f + state.visualYOffset, 0);
        poseStack.scale(state.scale, state.scale, state.scale);
        poseStack.scale(.6f, .6f, .6f);

        poseStack.pushPose();
        poseStack.scale(.4f, .4f, .4f);

        Vec3 orbGradient1 = green.add((yellow.subtract(green)).scale(state.orbColorPeriod));
        Vec3 orbGradient2 = white.add((purple.subtract(white)).scale(state.orbColorPeriod));
        Vec3 orbGradient3 = orbGradient1.add((orbGradient2.subtract(orbGradient1)).scale(state.colorPeriod));

        poseStack.mulPose(Axis.XP.rotationDegrees(state.swirlX * .45f));
        poseStack.mulPose(Axis.YP.rotationDegrees(state.swirlY * .45f));
        poseStack.mulPose(Axis.ZP.rotationDegrees(state.swirlZ * .45f));

        int orbColor = colorf((float) orbGradient3.x, (float) orbGradient3.y, (float) orbGradient3.z, 1f);
        renderPart(collector, poseStack, RenderTypes.entityCutout(SOLID_TEXTURE), orb, state.lightCoords, orbColor);
        poseStack.popPose();

        poseStack.mulPose(Axis.XP.rotationDegrees(state.swirlX));
        poseStack.mulPose(Axis.YP.rotationDegrees(state.swirlY));
        poseStack.mulPose(Axis.ZP.rotationDegrees(state.swirlZ));

        Vec3 rimGradient = green.add((yellow.subtract(green)).scale(state.colorPeriod));
        Vec3 rimGradientInverted = green.add((yellow.subtract(green)).scale(1 - state.colorPeriod));
        int rimColor = colorf((float) rimGradient.x, (float) rimGradient.y, (float) rimGradient.z, 1f);
        renderPart(collector, poseStack, RenderTypes.entityCutout(ORB_TEXTURE), orb, state.lightCoords, rimColor);

        poseStack.mulPose(Axis.XP.rotationDegrees(state.swirlZ));
        poseStack.mulPose(Axis.YP.rotationDegrees(state.swirlX));
        poseStack.mulPose(Axis.ZP.rotationDegrees(state.swirlY));

        poseStack.scale(1.5f, 1.5f, 1.5f);
        int rimInvertedColor = colorf((float) rimGradientInverted.x, (float) rimGradientInverted.y, (float) rimGradientInverted.z, 1f);
        renderPart(collector, poseStack, RenderTypes.entityCutout(ORB_TEXTURE), swirl, state.lightCoords, rimInvertedColor);

        poseStack.popPose();
    }

    private static void renderPart(SubmitNodeCollector collector, PoseStack poseStack, net.minecraft.client.renderer.rendertype.RenderType renderType, ModelPart part, int lightCoords, int color) {
        collector.order(0).submitCustomGeometry(poseStack, renderType, (pose, consumer) -> {
            PoseStack partPose = new PoseStack();
            partPose.last().set(pose);
            part.render(partPose, consumer, FULL_BRIGHT, OverlayTexture.NO_OVERLAY, color);
        });
    }

    public static int colorf(float pRed, float pGreen, float pBlue, float pAlpha) {
        return ((int) (255 * pAlpha) << 24) | ((int) (255 * pRed) << 16) | ((int) (255 * pGreen) << 8) | (int) (255 * pBlue);
    }
}
