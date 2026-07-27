package invmod.client.render;

import invmod.InvasionMod;
import invmod.client.model.IMEggModel;
import invmod.client.model.IMImpModel;
import invmod.client.model.IMThrowerModel;
import invmod.client.model.IMVultureModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Mob;

/** Renderers using our own ported {@link net.minecraft.client.model.EntityModel}s
 *  for entities that previously fell back to ZombieRenderer. */
public final class IMCustomRenderers {
    private IMCustomRenderers() {}

    private static ResourceLocation tex(String name) {
        return ResourceLocation.fromNamespaceAndPath(InvasionMod.MODID, "textures/entity/" + name + ".png");
    }

    public static class IMEggRenderer extends MobRenderer<Mob, IMEggModel> {
        private static final ResourceLocation T = tex("spider_egg");
        public IMEggRenderer(EntityRendererProvider.Context ctx) {
            super(ctx, new IMEggModel(ctx.bakeLayer(IMEggModel.LAYER)), 0.4F);
        }
        @Override public ResourceLocation getTextureLocation(Mob e) { return T; }
    }

    public static class IMImpRenderer extends MobRenderer<Mob, IMImpModel> {
        private static final ResourceLocation T = tex("imp");
        public IMImpRenderer(EntityRendererProvider.Context ctx) {
            super(ctx, new IMImpModel(ctx.bakeLayer(IMImpModel.LAYER)), 0.3F);
        }
        @Override public ResourceLocation getTextureLocation(Mob e) { return T; }
        @Override
        protected void scale(Mob e, com.mojang.blaze3d.vertex.PoseStack pose, float partial) {
            pose.scale(0.7F, 0.7F, 0.7F);
        }
    }

    public static class IMBurrowerRenderer extends MobRenderer<Mob, invmod.client.model.IMBurrowerModel> {
        private static final ResourceLocation T = tex("burrower");
        public IMBurrowerRenderer(EntityRendererProvider.Context ctx) {
            super(ctx, new invmod.client.model.IMBurrowerModel(ctx.bakeLayer(invmod.client.model.IMBurrowerModel.LAYER)), 0.6F);
        }
        @Override public ResourceLocation getTextureLocation(Mob e) { return T; }
    }

    public static class IMThrowerRenderer extends MobRenderer<Mob, IMThrowerModel> {
        private static final ResourceLocation T = tex("thrower_t1");
        public IMThrowerRenderer(EntityRendererProvider.Context ctx) {
            super(ctx, new IMThrowerModel(ctx.bakeLayer(IMThrowerModel.LAYER)), 1.0F);
        }
        @Override public ResourceLocation getTextureLocation(Mob e) { return T; }
        @Override
        protected void scale(Mob e, com.mojang.blaze3d.vertex.PoseStack pose, float partial) {
            // Original is a squat-tall figure ~ same size as zombie but wider.
            pose.scale(1.4F, 1.4F, 1.4F);
        }
    }

    public static class IMVultureRenderer extends MobRenderer<Mob, IMVultureModel> {
        private static final ResourceLocation T = tex("vulture");
        private final float scale;
        public IMVultureRenderer(EntityRendererProvider.Context ctx, float scale) {
            super(ctx, new IMVultureModel(ctx.bakeLayer(IMVultureModel.LAYER_VULTURE)), 1.2F * scale);
            this.scale = scale;
        }
        @Override public ResourceLocation getTextureLocation(Mob e) { return T; }
        @Override
        protected void scale(Mob e, com.mojang.blaze3d.vertex.PoseStack pose, float partial) {
            pose.scale(scale, scale, scale);
        }
    }
}
