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
 * Ported from 1.7.2 {@code ModelBurrower}. A four-segment worm-like
 * creature: head + seg1 + seg2 + seg3, each a 4x5x5 box, chained behind
 * the head. The original used a custom bone array for fluid undulation
 * driven by a {@code PosRotate3D} chain; this port replaces that with a
 * sinusoidal sway based on limb-swing and age, which is visually similar
 * and survives without the bone-array porting effort.
 */
public class IMBurrowerModel extends EntityModel<Mob> {
    public static final ModelLayerLocation LAYER = new ModelLayerLocation(
            ResourceLocation.fromNamespaceAndPath(InvasionMod.MODID, "im_burrower"), "main");

    private final ModelPart head;
    private final ModelPart seg1;
    private final ModelPart seg2;
    private final ModelPart seg3;

    public IMBurrowerModel(ModelPart root) {
        this.head = root.getChild("head");
        this.seg1 = root.getChild("seg1");
        this.seg2 = root.getChild("seg2");
        this.seg3 = root.getChild("seg3");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition r = mesh.getRoot();
        r.addOrReplaceChild("head",
                CubeListBuilder.create().texOffs(0, 0).addBox(-2F, -2.5F, -2.5F, 4F, 5F, 5F),
                PartPose.offset(8F, 18F, 0F));
        r.addOrReplaceChild("seg1",
                CubeListBuilder.create().texOffs(0, 0).addBox(-2F, -2.5F, -2.5F, 4F, 5F, 5F),
                PartPose.offset(4F, 18F, 0F));
        r.addOrReplaceChild("seg2",
                CubeListBuilder.create().texOffs(0, 0).addBox(-2F, -2.5F, -2.5F, 4F, 5F, 5F),
                PartPose.offset(0F, 18F, 0F));
        r.addOrReplaceChild("seg3",
                CubeListBuilder.create().texOffs(0, 0).addBox(-2F, -2.5F, -2.5F, 4F, 5F, 5F),
                PartPose.offset(-4F, 18F, 0F));
        // burrower.png is 64x32; we don't pad it so file matches design.
        return LayerDefinition.create(mesh, 64, 32);
    }

    @Override
    public void setupAnim(Mob entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        // Undulation: head + segments sway side to side, each with a phase offset.
        float base = limbSwing * 0.6F;
        float amp  = 0.3F + limbSwingAmount * 0.4F;
        head.yRot = Mth.sin(base) * amp;
        seg1.yRot = Mth.sin(base - 0.8F) * amp;
        seg2.yRot = Mth.sin(base - 1.6F) * amp;
        seg3.yRot = Mth.sin(base - 2.4F) * amp;
        // Look-at affects the head pitch only.
        head.xRot = headPitch / 57.29578F;
    }

    @Override
    public void renderToBuffer(PoseStack pose, VertexConsumer buf, int packedLight, int packedOverlay, int color) {
        head.render(pose, buf, packedLight, packedOverlay, color);
        seg1.render(pose, buf, packedLight, packedOverlay, color);
        seg2.render(pose, buf, packedLight, packedOverlay, color);
        seg3.render(pose, buf, packedLight, packedOverlay, color);
    }
}
