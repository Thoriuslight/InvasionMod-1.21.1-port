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
 * Faithful port of 1.7.2 {@code ModelVulture}. Hierarchy and UV coordinates
 * preserved exactly so the original 128x128 vulture texture maps correctly.
 * Walking animation moves thighs; flying flaps wings.
 */
public class IMVultureModel extends EntityModel<Mob> {
    public static final ModelLayerLocation LAYER_VULTURE = new ModelLayerLocation(
            ResourceLocation.fromNamespaceAndPath(InvasionMod.MODID, "im_vulture"), "main");

    private final ModelPart body;
    private final ModelPart rightThigh;
    private final ModelPart leftThigh;
    private final ModelPart leftWing1;
    private final ModelPart rightWing1;
    private final ModelPart neck1;
    private final ModelPart head;

    public IMVultureModel(ModelPart root) {
        this.body = root.getChild("body");
        this.rightThigh = body.getChild("rightThigh");
        this.leftThigh  = body.getChild("leftThigh");
        this.leftWing1  = body.getChild("leftWing1");
        this.rightWing1 = body.getChild("rightWing1");
        this.neck1      = body.getChild("neck1");
        this.head       = neck1.getChild("neck2").getChild("neck3").getChild("head");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition r = mesh.getRoot();

        PartDefinition body = r.addOrReplaceChild("body",
                CubeListBuilder.create().texOffs(0, 0)
                        .addBox(-10F, -10F, -10F, 20F, 30F, 20F),
                PartPose.offsetAndRotation(0F, -19F, 0F, 0.7F, 0F, 0F));

        // ---- Legs (thigh→leg→ankle→toes×4 with claws). Right side. -------
        PartDefinition rightThigh = body.addOrReplaceChild("rightThigh",
                CubeListBuilder.create().texOffs(84, 82)
                        .addBox(-4.5F, -3.5F, -4.5F, 9F, 15F, 9F),
                PartPose.offsetAndRotation(-5F, 20F, -2F, -0.39F, 0F, 0.09F));
        PartDefinition rightLeg = rightThigh.addOrReplaceChild("rightLeg",
                CubeListBuilder.create().texOffs(56, 50)
                        .addBox(-2F, -3F, -2F, 4F, 16F, 4F),
                PartPose.offsetAndRotation(0F, 11F, 0F, -0.72F, 0F, 0F));
        PartDefinition rightAnkle = rightLeg.addOrReplaceChild("rightAnkle",
                CubeListBuilder.create(), PartPose.offsetAndRotation(0F, 12F, 0F, 0.1F, 0.2F, 0F));
        addToe(rightAnkle, "rightToeB", 60, 0,  0.5F, 0.0F,  0F, 0F, 2F, 1.34F, 0F, 0F, -1F, -1F, -1F, 2, 8, 2, 0F, 6F, 0F);
        addToe(rightAnkle, "rightToeR", 0, 0,  0.0F, 0.0F,  1F, 0F, 1F, -0.8F, -0.28F, -0.28F, -1F, -0.5F, -1F, 2, 9, 2, 0F, 8F, 0F);
        addToe(rightAnkle, "rightToeM", 8, 0,  0.0F, 0.0F,  0F, 0F, 0F, -0.8F, 0F, 0F, -1F, 0F, -1F, 2, 10, 2, 0F, 9F, 0F);
        addToe(rightAnkle, "rightToeL", 0, 0,  -0.5F, 0.0F,  0F, 0F, 1F, -0.8F, 0.28F, 0.28F, -1F, 0.5F, -1F, 2, 9, 2, 0F, 9F, 0F);

        PartDefinition leftThigh = body.addOrReplaceChild("leftThigh",
                CubeListBuilder.create().mirror().texOffs(84, 82)
                        .addBox(-4.5F, -3.5F, -4.5F, 9F, 15F, 9F),
                PartPose.offsetAndRotation(5F, 20F, -2F, -0.39F, 0F, -0.09F));
        PartDefinition leftLeg = leftThigh.addOrReplaceChild("leftLeg",
                CubeListBuilder.create().mirror().texOffs(56, 50)
                        .addBox(-2F, -3F, -2F, 4F, 16F, 4F),
                PartPose.offsetAndRotation(0F, 11F, 0F, -0.72F, 0F, 0F));
        PartDefinition leftAnkle = leftLeg.addOrReplaceChild("leftAnkle",
                CubeListBuilder.create(), PartPose.offsetAndRotation(0F, 12F, 0F, 0.1F, -0.2F, 0F));
        addToe(leftAnkle, "leftToeB", 60, 0,  0.5F, 0.0F,  0F, 0F, 2F, 1.34F, 0F, 0F, -1F, -1F, -1F, 2, 8, 2, 0F, 6F, 0F);
        addToe(leftAnkle, "leftToeR", 0, 0,  0.0F, 0.0F,  -1F, 0F, 1F, -0.8F, 0.28F, 0.28F, -1F, -0.5F, -1F, 2, 9, 2, 0F, 8F, 0F);
        addToe(leftAnkle, "leftToeM", 8, 0,  0.0F, 0.0F,  0F, 0F, 0F, -0.8F, 0F, 0F, -1F, 0F, -1F, 2, 10, 2, 0F, 9F, 0F);
        addToe(leftAnkle, "leftToeL", 0, 0,  0.5F, 0.0F,  0F, 0F, 1F, -0.8F, -0.28F, -0.28F, -1F, 0.5F, -1F, 2, 9, 2, 0F, 9F, 0F);

        // ---- Neck chain (1→2→3→head→beaks) -------------------------------
        PartDefinition neck1 = body.addOrReplaceChild("neck1",
                CubeListBuilder.create().texOffs(43, 95)
                        .addBox(-7F, -7F, -6.5F, 14F, 10F, 13F),
                PartPose.offset(0F, -10F, 1F));
        PartDefinition neck2 = neck1.addOrReplaceChild("neck2",
                CubeListBuilder.create().texOffs(50, 73)
                        .addBox(-5F, -4F, -5F, 10F, 8F, 10F),
                PartPose.offset(0F, -8F, 0F));
        PartDefinition neck3 = neck2.addOrReplaceChild("neck3",
                CubeListBuilder.create().texOffs(80, 65)
                        .addBox(-4F, -5.5F, -5F, 8F, 5F, 10F),
                PartPose.offset(0F, -2F, 0F));
        PartDefinition head = neck3.addOrReplaceChild("head",
                CubeListBuilder.create().texOffs(14, 108)
                        .addBox(-4.5F, -5F, -9.5F, 9F, 8F, 11F),
                PartPose.offset(0F, -4F, 0F));
        PartDefinition upperBeak = head.addOrReplaceChild("upperBeak",
                CubeListBuilder.create().texOffs(54, 118)
                        .addBox(-2.5F, -1F, -5F, 5F, 2F, 8F),
                PartPose.offset(0F, -0.8F, -10F));
        upperBeak.addOrReplaceChild("upperBeakTip",
                CubeListBuilder.create().texOffs(72, 118)
                        .addBox(-1F, -1F, -1F, 2F, 2F, 2F),
                PartPose.offset(0F, 0F, -6F));
        PartDefinition lowerBeak = head.addOrReplaceChild("lowerBeak",
                CubeListBuilder.create().texOffs(80, 118)
                        .addBox(-2.5F, -1F, -5F, 5F, 2F, 8F),
                PartPose.offset(0F, 1.5F, -10F));
        lowerBeak.addOrReplaceChild("lowerBeakTip",
                CubeListBuilder.create().texOffs(78, 121)
                        .addBox(-1F, -0.5F, -1F, 2F, 1F, 2F),
                PartPose.offset(0F, -0.5F, -6F));

        // ---- Wings (left mirrored) ---------------------------------------
        PartDefinition leftWing1 = body.addOrReplaceChild("leftWing1",
                CubeListBuilder.create().mirror().texOffs(0, 50)
                        .addBox(-0.5F, -4.5F, -1.5F, 25F, 29F, 3F),
                PartPose.offset(7F, -8F, 6F));
        PartDefinition leftWing2 = leftWing1.addOrReplaceChild("leftWing2",
                CubeListBuilder.create().mirror().texOffs(0, 82)
                        .addBox(-2.5F, -5F, -1F, 23F, 24F, 2F),
                PartPose.offset(23F, 1F, 0F));
        leftWing2.addOrReplaceChild("leftWing3",
                CubeListBuilder.create().mirror().texOffs(80, 0)
                        .addBox(-2.5F, -5F, -0.5F, 23F, 22F, 1F),
                PartPose.offset(21F, 0.2F, 0.3F));

        PartDefinition rightWing1 = body.addOrReplaceChild("rightWing1",
                CubeListBuilder.create().texOffs(0, 50)
                        .addBox(-24.5F, -4.5F, -1.5F, 25F, 29F, 3F),
                PartPose.offset(-7F, -8F, 6F));
        PartDefinition rightWing2 = rightWing1.addOrReplaceChild("rightWing2",
                CubeListBuilder.create().texOffs(0, 82)
                        .addBox(-20.5F, -5F, -1F, 23F, 24F, 2F),
                PartPose.offset(-23F, 1F, 0F));
        rightWing2.addOrReplaceChild("rightWing3",
                CubeListBuilder.create().texOffs(80, 0)
                        .addBox(-20.5F, -5F, -0.5F, 23F, 22F, 1F),
                PartPose.offset(-21F, 0.2F, 0.3F));

        // ---- Tail --------------------------------------------------------
        body.addOrReplaceChild("tail",
                CubeListBuilder.create().texOffs(80, 23)
                        .addBox(-8.5F, -5F, -1F, 17F, 40F, 2F),
                PartPose.offset(0F, 19F, 8F));

        return LayerDefinition.create(mesh, 128, 128);
    }

    private static void addToe(PartDefinition parent, String name, int u, int v,
                               float pxOffset, float pyOffset, float pzOffset,
                               float padX, float padY, float toeRotX, float toeRotY, float toeRotZ,
                               float bx, float by, float bz, int sx, int sy, int sz,
                               float clawX, float clawY, float clawZ) {
        PartDefinition toe = parent.addOrReplaceChild(name,
                CubeListBuilder.create().texOffs(u, v)
                        .addBox(bx, by, bz, sx, sy, sz),
                PartPose.offsetAndRotation(pxOffset, pyOffset + padY, pzOffset + padX, toeRotX, toeRotY, toeRotZ));
        toe.addOrReplaceChild(name + "Claw",
                CubeListBuilder.create().texOffs(0, 11)
                        .addBox(-0.5F, 0F, -1F, 1F, 4F, 2F),
                PartPose.offset(clawX, clawY, clawZ));
    }

    @Override
    public void setupAnim(Mob entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        head.yRot = netHeadYaw / 57.29578F;
        head.xRot = headPitch / 57.29578F;

        // Wing flap (constant idle + larger when moving).
        float flap = Mth.cos(ageInTicks * 0.18F) * 0.5F
                + Mth.cos(limbSwing * 0.6662F) * 1.0F * limbSwingAmount;
        leftWing1.zRot  = -flap * 0.6F;
        rightWing1.zRot =  flap * 0.6F;

        // Leg shuffle on ground.
        float legSwing = Mth.cos(limbSwing * 0.6662F) * 0.8F * limbSwingAmount;
        leftThigh.xRot  = -0.39F + legSwing;
        rightThigh.xRot = -0.39F - legSwing;
    }

    @Override
    public void renderToBuffer(PoseStack pose, VertexConsumer buf, int packedLight, int packedOverlay, int color) {
        body.render(pose, buf, packedLight, packedOverlay, color);
    }
}
