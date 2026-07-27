package invmod.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import invmod.InvasionMod;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Mob;

/** Hunched bipedal imp with horns and tail, ported from 1.7.2 ModelImp. */
public class IMImpModel extends EntityModel<Mob> {
    public static final ModelLayerLocation LAYER = new ModelLayerLocation(
            ResourceLocation.fromNamespaceAndPath(InvasionMod.MODID, "im_imp"), "main");

    private final ModelPart root;
    private final ModelPart head;
    private final ModelPart rightArm;
    private final ModelPart leftArm;
    private final ModelPart rightLeg;
    private final ModelPart rightShin;
    private final ModelPart rightFoot;
    private final ModelPart leftLeg;
    private final ModelPart leftShin;
    private final ModelPart leftFoot;

    public IMImpModel(ModelPart root) {
        this.root = root;
        this.head = root.getChild("head");
        this.rightArm = root.getChild("rightArm");
        this.leftArm = root.getChild("leftArm");
        this.rightLeg = root.getChild("rightLeg");
        this.rightShin = root.getChild("rightShin");
        this.rightFoot = root.getChild("rightFoot");
        this.leftLeg = root.getChild("leftLeg");
        this.leftShin = root.getChild("leftShin");
        this.leftFoot = root.getChild("leftFoot");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition r = mesh.getRoot();

        PartDefinition head = r.addOrReplaceChild("head",
                CubeListBuilder.create()
                        .texOffs(44, 0).addBox(-2.7333F, -3.0F, -2.0F, 5F, 3F, 4F),
                PartPose.offsetAndRotation(-0.4F, 9.8F, -3.3F, 0.15807F, 0F, 0F));
        head.addOrReplaceChild("rhorn",
                CubeListBuilder.create().texOffs(0, 0).addBox(1.0F, -4.0F, 1.5F, 1F, 1F, 1F), PartPose.ZERO);
        head.addOrReplaceChild("lhorn",
                CubeListBuilder.create().texOffs(0, 2).addBox(-1.0F, -4.0F, 1.5F, 1F, 1F, 1F), PartPose.ZERO);

        r.addOrReplaceChild("body",
                CubeListBuilder.create().texOffs(23, 1).addBox(-4.0F, 0F, -4.0F, 7F, 4F, 3F),
                PartPose.offsetAndRotation(0F, 9.1F, -0.8666F, 0.64346F, 0F, 0F));
        r.addOrReplaceChild("bodyMid",
                CubeListBuilder.create().texOffs(1, 1).addBox(0F, 0F, 0F, 7F, 5F, 3F),
                PartPose.offsetAndRotation(-4.0F, 12.4666F, -2.2666F, -0.15807F, 0F, 0F));
        r.addOrReplaceChild("bodyChest",
                CubeListBuilder.create().texOffs(0, 9).addBox(0F, -1F, 0F, 7F, 6F, 2F),
                PartPose.offsetAndRotation(-4.0F, 12.3666F, -3.8F, 0.31614F, 0F, 0F));
        r.addOrReplaceChild("neck",
                CubeListBuilder.create().texOffs(44, 7).addBox(0F, 0F, 0F, 3F, 2F, 2F),
                PartPose.offsetAndRotation(-2.0F, 9.6F, -4.0333F, 0.27662F, 0F, 0F));

        r.addOrReplaceChild("rightArm",
                CubeListBuilder.create().texOffs(26, 9).addBox(-2F, -0.7333F, -1.1333F, 2F, 7F, 2F),
                PartPose.offset(-4F, 10.8F, -2.0666F));
        r.addOrReplaceChild("leftArm",
                CubeListBuilder.create().texOffs(18, 9).addBox(0F, -0.8666F, -1F, 2F, 7F, 2F),
                PartPose.offset(3F, 10.8F, -2.1F));

        r.addOrReplaceChild("rightLeg",
                CubeListBuilder.create().texOffs(0, 17).addBox(-1F, 0F, -2F, 2F, 4F, 3F),
                PartPose.offsetAndRotation(-2F, 16.9F, -1F, -0.15807F, 0F, 0F));
        r.addOrReplaceChild("rightShin",
                CubeListBuilder.create().texOffs(10, 17).addBox(-2F, 0.6F, -4.4F, 2F, 3F, 2F),
                PartPose.offsetAndRotation(-1F, 16.9F, -1F, 0.82623F, 0F, 0F));
        r.addOrReplaceChild("rightFoot",
                CubeListBuilder.create().texOffs(18, 18).addBox(-2F, 4.2F, -1F, 2F, 3F, 2F),
                PartPose.offsetAndRotation(-1F, 16.9F, -1F, -0.01403F, 0F, 0F));
        r.addOrReplaceChild("leftLeg",
                CubeListBuilder.create().texOffs(0, 24).addBox(-1F, 0F, -2F, 2F, 4F, 3F),
                PartPose.offsetAndRotation(1F, 17F, -1F, -0.15919F, 0F, 0F));
        r.addOrReplaceChild("leftShin",
                CubeListBuilder.create().texOffs(10, 22).addBox(-1F, 0.6F, -4.4333F, 2F, 3F, 2F),
                PartPose.offsetAndRotation(1F, 17F, -1F, 0.82461F, 0F, 0F));
        r.addOrReplaceChild("leftFoot",
                CubeListBuilder.create().texOffs(10, 27).addBox(-1F, 4.2F, -1F, 2F, 3F, 2F),
                PartPose.offsetAndRotation(1F, 17F, -1F, -0.01214F, 0F, 0F));

        r.addOrReplaceChild("tail",
                CubeListBuilder.create().texOffs(18, 23).addBox(0F, 0F, 0F, 1F, 8F, 1F),
                PartPose.offsetAndRotation(-1F, 15F, -0.6666F, 0.47304F, 0F, 0F));
        r.addOrReplaceChild("tail2",
                CubeListBuilder.create().texOffs(22, 23).addBox(0F, 0F, 0F, 1F, 4F, 1F),
                PartPose.offsetAndRotation(-1F, 22.1F, 2.9F, 1.38309F, 0F, 0F));

        // imp.png was padded from original 64x32 to 64x64; texture file is now
        // 64x64 so model normalization must match the file, not the design size.
        return LayerDefinition.create(mesh, 64, 64);
    }

    @Override
    public void setupAnim(Mob entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        head.yRot = netHeadYaw / 57.29578F;
        head.xRot = headPitch / 57.29578F;

        rightArm.xRot = Mth.cos(limbSwing * 0.6662F + Mth.PI) * 2F * limbSwingAmount * 0.5F;
        leftArm.xRot  = Mth.cos(limbSwing * 0.6662F) * 2F * limbSwingAmount * 0.5F;
        rightArm.zRot = Mth.cos(ageInTicks * 0.09F) * 0.05F + 0.05F;
        leftArm.zRot  = -Mth.cos(ageInTicks * 0.09F) * 0.05F - 0.05F;
        rightArm.xRot += Mth.sin(ageInTicks * 0.067F) * 0.05F;
        leftArm.xRot  -= Mth.sin(ageInTicks * 0.067F) * 0.05F;

        float swing = Mth.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount;
        rightLeg.xRot  = swing - 0.158F;
        rightShin.xRot = swing + 0.82623F;
        rightFoot.xRot = swing - 0.01403F;
        float swingL = Mth.cos(limbSwing * 0.6662F + Mth.PI) * 1.4F * limbSwingAmount;
        leftLeg.xRot  = swingL - 0.15919F;
        leftShin.xRot = swingL + 0.82461F;
        leftFoot.xRot = swingL - 0.01214F;
    }

    @Override
    public void renderToBuffer(PoseStack pose, VertexConsumer buf, int packedLight, int packedOverlay, int color) {
        root.render(pose, buf, packedLight, packedOverlay, color);
    }
}
