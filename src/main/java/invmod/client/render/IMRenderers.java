package invmod.client.render;

import invmod.InvasionMod;
import invmod.entity.IMSpiderEntity;
import net.minecraft.client.renderer.entity.CreeperRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.SkeletonRenderer;
import net.minecraft.client.renderer.entity.SpiderRenderer;
import net.minecraft.client.renderer.entity.WolfRenderer;
import net.minecraft.client.renderer.entity.ZombieRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.entity.monster.AbstractSkeleton;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Zombie;

/**
 * Phase-G renderer subclasses. Each one only overrides {@code getTextureLocation}
 * to point at our texture; the model + layers are inherited from the vanilla
 * renderer of the closest ancestor mob, so entities are visible in dev without
 * any 1.7.2 ModelBase code yet. Renderer–entity pairing relies on the entity
 * class extending the matching vanilla mob (Zombie / Skeleton / etc.).
 */
public final class IMRenderers {
    private IMRenderers() {}

    private static ResourceLocation tex(String name) {
        return ResourceLocation.fromNamespaceAndPath(InvasionMod.MODID, "textures/entity/" + name + ".png");
    }

    // ---- Zombie family (also covers ZombifiedPiglin entities since
    //       ZombifiedPiglin is-a Zombie in vanilla hierarchy) -------------

    public static final class IMZombie extends ZombieRenderer {
        private static final ResourceLocation T = tex("zombie_t1a");
        public IMZombie(EntityRendererProvider.Context c) { super(c); }
        @Override public ResourceLocation getTextureLocation(Zombie e) { return T; }
    }

    public static final class IMThrower extends ZombieRenderer {
        private static final ResourceLocation T = tex("thrower_t1");
        public IMThrower(EntityRendererProvider.Context c) { super(c); }
        @Override public ResourceLocation getTextureLocation(Zombie e) { return T; }
    }

    public static final class IMBurrower extends ZombieRenderer {
        private static final ResourceLocation T = tex("burrower");
        public IMBurrower(EntityRendererProvider.Context c) { super(c); }
        @Override public ResourceLocation getTextureLocation(Zombie e) { return T; }
    }

    public static final class IMImp extends ZombieRenderer {
        private static final ResourceLocation T = tex("imp");
        public IMImp(EntityRendererProvider.Context c) { super(c); }
        @Override public ResourceLocation getTextureLocation(Zombie e) { return T; }
    }

    public static final class IMBird extends ZombieRenderer {
        private static final ResourceLocation T = tex("vulture");
        public IMBird(EntityRendererProvider.Context c) { super(c); }
        @Override public ResourceLocation getTextureLocation(Zombie e) { return T; }
    }

    public static final class IMGiantBird extends ZombieRenderer {
        private static final ResourceLocation T = tex("vulture");
        public IMGiantBird(EntityRendererProvider.Context c) { super(c); }
        @Override public ResourceLocation getTextureLocation(Zombie e) { return T; }
    }

    public static final class IMEgg extends ZombieRenderer {
        private static final ResourceLocation T = tex("spider_egg");
        public IMEgg(EntityRendererProvider.Context c) { super(c); }
        @Override public ResourceLocation getTextureLocation(Zombie e) { return T; }
    }

    public static final class IMZombiePigman extends ZombieRenderer {
        private static final ResourceLocation T = tex("zombie_pigman_t3");
        public IMZombiePigman(EntityRendererProvider.Context c) { super(c); }
        @Override public ResourceLocation getTextureLocation(Zombie e) { return T; }
    }

    public static final class IMPigEngy extends ZombieRenderer {
        private static final ResourceLocation T = tex("pig_engy_t1");
        public IMPigEngy(EntityRendererProvider.Context c) { super(c); }
        @Override public ResourceLocation getTextureLocation(Zombie e) { return T; }
    }

    // ---- Skeleton family -------------------------------------------------

    public static final class IMSkeleton extends SkeletonRenderer {
        private static final ResourceLocation T = ResourceLocation.parse("minecraft:textures/entity/skeleton/skeleton.png");
        public IMSkeleton(EntityRendererProvider.Context c) { super(c); }
        @Override public ResourceLocation getTextureLocation(AbstractSkeleton e) { return T; }
    }

    // ---- Spider family ---------------------------------------------------

    public static final class IMSpider extends SpiderRenderer<IMSpiderEntity> {
        private static final ResourceLocation T = tex("spider_t2");
        public IMSpider(EntityRendererProvider.Context c) { super(c); }
        @Override public ResourceLocation getTextureLocation(IMSpiderEntity e) { return T; }
    }

    // ---- Creeper family --------------------------------------------------

    public static final class IMCreeper extends CreeperRenderer {
        private static final ResourceLocation T = ResourceLocation.parse("minecraft:textures/entity/creeper/creeper.png");
        public IMCreeper(EntityRendererProvider.Context c) { super(c); }
        @Override public ResourceLocation getTextureLocation(Creeper e) { return T; }
    }

    // ---- Wolf family -----------------------------------------------------

    public static final class IMWolf extends WolfRenderer {
        private static final ResourceLocation T = tex("wolf_tame_nexus");
        public IMWolf(EntityRendererProvider.Context c) { super(c); }
        @Override public ResourceLocation getTextureLocation(Wolf e) { return T; }
    }
}
