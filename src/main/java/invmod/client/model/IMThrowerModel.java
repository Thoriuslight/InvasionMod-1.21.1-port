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

/**
 * Faithful port of 1.7.2 {@code ModelThrower}. Squat torso (12 wide, 4 tall)
 * with a small flat head, oversized boulder-throwing right arm, vestigial
 * left arm, stubby legs. Texture is 64x32 originally (now padded to 128x128
 * but UV coords stay in the 64x32 layout because we kept texOffs unchanged).
 */
public class IMThrowerModel extends EntityModel<Mob> {
    public static final ModelLayerLocation LAYER = new ModelLayerLocation(
            ResourceLocation.fromNamespaceAndPath(InvasionMod.MODID, "im_thrower"), "main");

    private final ModelPart root;
    private final ModelPart head;
    private final ModelPart rightArm;
    private final ModelPart leftArm;
    private final ModelPart rightLeg;
    private final ModelPart leftLeg;

    public IMThrowerModel(ModelPart root) {
        this.root = root;
        this.head     = root.getChild("head");
        this.rightArm = root.getChild("rightArm");
        this.leftArm  = root.getChild("leftArm");
        this.rightLeg = root.getChild("rightLeg");
        this.leftLeg  = root.getChild("leftLeg");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition r = mesh.getRoot();

        r.addOrReplaceChild("head",
                CubeListBuilder.create().texOffs(16, 14).addBox(-2F, -2F, -2F, 4F, 2F, 4F),
                PartPose.offset(0F, 16F, 4F));
        r.addOrReplaceChild("body",
                CubeListBuilder.create().texOffs(0, 1).addBox(-7F, 2F, -4F, 12F, 4F, 9F),
                PartPose.offset(-0.4F, 16F, 3F));
        r.addOrReplaceChild("body2",
                CubeListBuilder.create().texOffs(0, 23).addBox(-3.6666F, 0F, 0F, 12F, 2F, 7F),
                PartPose.offset(-3F, 16F, 0F));
        r.addOrReplaceChild("rightArm",
                CubeListBuilder.create().texOffs(39, 22).addBox(-3F, 0F, -1.4666F, 3F, 7F, 3F),
                PartPose.offset(-6.5666F, 16F, 5F));
        r.addOrReplaceChild("leftArm",
                CubeListBuilder.create().texOffs(40, 16).addBox(0F, 0F, -1F, 2F, 4F, 2F),
                PartPose.offset(5F, 16F, 5F));
        r.addOrReplaceChild("rightLeg",
                CubeListBuilder.create().texOffs(0, 14).addBox(-2F, 0F, -2F, 4F, 2F, 4F),
                PartPose.offset(-4.0666F, 22F, 4F));
        r.addOrReplaceChild("leftLeg",
                CubeListBuilder.create().texOffs(0, 14).addBox(-2F, 0F, -2F, 4F, 2F, 4F),
                PartPose.offset(3F, 22F, 4F));

        // thrower_t1.png was 128x64 (= 2x high-res of a 64x32 design layout),
        // then padded to 128x128. Model UVs are in design (64x32) pixel coords;
        // file is 2x. Correct textureSize for normalization is (64, 64) so
        // texOffs(16, 14) reads pixel (32, 28) of the 128x128 file.
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

        rightLeg.xRot = Mth.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount;
        leftLeg.xRot  = Mth.cos(limbSwing * 0.6662F + Mth.PI) * 1.4F * limbSwingAmount;
    }

    @Override
    public void renderToBuffer(PoseStack pose, VertexConsumer buf, int packedLight, int packedOverlay, int color) {
        root.render(pose, buf, packedLight, packedOverlay, color);
    }
}
