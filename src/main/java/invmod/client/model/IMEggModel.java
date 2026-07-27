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
import net.minecraft.world.entity.Mob;

/** Stepped pyramid egg shape ported from 1.7.2 ModelEgg. Static (no anim). */
public class IMEggModel extends EntityModel<Mob> {
    public static final ModelLayerLocation LAYER = new ModelLayerLocation(
            ResourceLocation.fromNamespaceAndPath(InvasionMod.MODID, "im_egg"), "main");

    private final ModelPart root;

    public IMEggModel(ModelPart root) { this.root = root; }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition r = mesh.getRoot();
        r.addOrReplaceChild("base",  CubeListBuilder.create().texOffs(0, 0).addBox(1F, 0F, 1F, 7F, 1F, 7F),  PartPose.ZERO);
        r.addOrReplaceChild("l1s1", CubeListBuilder.create().texOffs(0, 14).addBox(0F, 0F, 0F, 8F, 1F, 1F), PartPose.offset(1F, -1F, 8F));
        r.addOrReplaceChild("l1s2", CubeListBuilder.create().texOffs(28, 0).addBox(8F, -1F, 0F, 1F, 1F, 8F), PartPose.ZERO);
        r.addOrReplaceChild("l1s3", CubeListBuilder.create().texOffs(0, 14).addBox(0F, -1F, 0F, 8F, 1F, 1F), PartPose.ZERO);
        r.addOrReplaceChild("l1s4", CubeListBuilder.create().texOffs(28, 0).addBox(0F, -1F, 1F, 1F, 1F, 8F), PartPose.ZERO);
        r.addOrReplaceChild("l2s1", CubeListBuilder.create().texOffs(0, 16).addBox(0F, -5F, 9F, 9F, 4F, 1F), PartPose.ZERO);
        r.addOrReplaceChild("l2s2", CubeListBuilder.create().texOffs(20, 10).addBox(9F, -5F, 0F, 1F, 4F, 9F), PartPose.ZERO);
        r.addOrReplaceChild("l2s3", CubeListBuilder.create().texOffs(0, 16).addBox(0F, -5F, -1F, 9F, 4F, 1F), PartPose.ZERO);
        r.addOrReplaceChild("l2s4", CubeListBuilder.create().texOffs(20, 10).addBox(-1F, -5F, 0F, 1F, 4F, 9F), PartPose.ZERO);
        r.addOrReplaceChild("l3s1", CubeListBuilder.create().texOffs(0, 21).addBox(1F, -7F, 8F, 8F, 2F, 1F), PartPose.ZERO);
        r.addOrReplaceChild("l3s2", CubeListBuilder.create().texOffs(10, 22).addBox(8F, -7F, 0F, 1F, 2F, 8F), PartPose.ZERO);
        r.addOrReplaceChild("l3s3", CubeListBuilder.create().texOffs(0, 21).addBox(0F, -7F, 0F, 8F, 2F, 1F), PartPose.ZERO);
        r.addOrReplaceChild("l3s4", CubeListBuilder.create().texOffs(10, 22).addBox(0F, -7F, 1F, 1F, 2F, 8F), PartPose.ZERO);
        r.addOrReplaceChild("l4s1", CubeListBuilder.create().texOffs(0, 24).addBox(2F, -10F, 7F, 6F, 3F, 1F), PartPose.ZERO);
        r.addOrReplaceChild("l4s2", CubeListBuilder.create().texOffs(28, 23).addBox(7F, -10F, 1F, 1F, 3F, 6F), PartPose.ZERO);
        r.addOrReplaceChild("l4s3", CubeListBuilder.create().texOffs(0, 24).addBox(1F, -10F, 1F, 6F, 3F, 1F), PartPose.ZERO);
        r.addOrReplaceChild("l4s4", CubeListBuilder.create().texOffs(28, 23).addBox(1F, -10F, 2F, 1F, 3F, 6F), PartPose.ZERO);
        r.addOrReplaceChild("top",   CubeListBuilder.create().texOffs(0, 8).addBox(2F, -11F, 2F, 5F, 1F, 5F),  PartPose.ZERO);
        // spider_egg.png padded 64x32 -> 64x64; match the file size.
        return LayerDefinition.create(mesh, 64, 64);
    }

    @Override
    public void setupAnim(Mob entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        // Static.
    }

    @Override
    public void renderToBuffer(PoseStack pose, VertexConsumer buf, int packedLight, int packedOverlay, int color) {
        root.render(pose, buf, packedLight, packedOverlay, color);
    }
}
