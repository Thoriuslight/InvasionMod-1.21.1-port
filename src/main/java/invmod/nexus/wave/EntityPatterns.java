package invmod.nexus.wave;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;


import invmod.InvasionMod;
import invmod.ModEntities;
import net.minecraft.resources.ResourceLocation;

public interface EntityPatterns {
    Map<ResourceLocation, PatternType> REGISTRY = new HashMap<>();

    EntityPattern ZOMBIE_T1_ANY = register("zombie_t1_any", new EntityPattern.Builder(ModEntities.IM_ZOMBIE.get()).addTier(1, 1).addFlavour(0, 3).addFlavour(1, 1), 1);
    EntityPattern ZOMBIE_T2_ANY_BASIC = register("zombie_t2_any_basic", new EntityPattern.Builder(ModEntities.IM_ZOMBIE.get()).addTier(2, 1).addFlavour(0, 2).addFlavour(1, 1).addFlavour(2, 0.4F), 1);
    EntityPattern ZOMBIE_T2_PLAIN = register("zombie_t2_plain",  new EntityPattern.Builder(ModEntities.IM_ZOMBIE.get()).addTier(2, 1).addFlavour(0, 1));
    EntityPattern ZOMBIE_T2_TAR = register("zombie_t2_tar", new EntityPattern.Builder(ModEntities.IM_ZOMBIE.get()).addTier(2, 1).addFlavour(2, 1).addTexture(5, 1));
    EntityPattern ZOMBIE_T3_ANY = register("zombie_t3_any", new EntityPattern.Builder(ModEntities.IM_ZOMBIE.get()).addTier(3, 1).addTexture(0, 1));

    EntityPattern ZOMBIE_PIGMAN_T1_ANY = register("zombie_pigman_t1_any", new EntityPattern.Builder(ModEntities.IM_ZOMBIE_PIGMAN.get()).addTier(1, 1).addFlavour(0, 1));
    EntityPattern ZOMBIE_PIGMAN_T2_ANY = register("zombie_pigman_t2_any", new EntityPattern.Builder(ModEntities.IM_ZOMBIE_PIGMAN.get()).addTier(2, 1).addFlavour(0, 1));
    EntityPattern ZOMBIE_PIGMAN_T3_ANY = register("zombie_pigman_t3_any", new EntityPattern.Builder(ModEntities.IM_ZOMBIE_PIGMAN.get()).addTier(3, 1).addFlavour(0, 1));

    EntityPattern SPIDER_T1_ANY = register("spider_t1_any", new EntityPattern.Builder(ModEntities.IM_SPIDER.get()), 0.5F);
    EntityPattern SPIDER_T2_ANY = register("spider_t2_any", new EntityPattern.Builder(ModEntities.IM_SPIDER.get())
            .addType(ModEntities.IM_JUMPING_SPIDER.get(), 1)
            .addType(ModEntities.IM_QUEEN_SPIDER.get(), 0.5F));
    EntityPattern SPIDER_T3_ANY = register("spider_t3_any", new EntityPattern.Builder(ModEntities.IM_JUMPING_SPIDER.get())
            .addType(ModEntities.IM_SPIDER.get(), 0.5F)
            .addType(ModEntities.IM_QUEEN_SPIDER.get(), 1));

    EntityPattern PIGMAN_ENGINEER_T1_ANY = register("pigman_engineer_t1_any", new EntityPattern.Builder(ModEntities.IM_PIGMAN_ENGINEER.get()).addTier(1, 1));

    EntityPattern SKELETON_T1_ANY = register("skeleton_t1_any", new EntityPattern.Builder(ModEntities.IM_SKELETON.get()).addTier(1, 1));

    EntityPattern THROWER_T1 = register("thrower_t1_any", new EntityPattern.Builder(ModEntities.IM_THROWER.get()).addTier(1, 1));
    EntityPattern THROWER_T2 = register("thrower_t2_any", new EntityPattern.Builder(ModEntities.IM_THROWER.get()).addTier(2, 1));

    EntityPattern BURROWER = register("burrower", new EntityPattern.Builder(ModEntities.IM_BURROWER.get()).addTier(1, 1));

    EntityPattern CREEPER_T1_BASIC = register("creeper_t1_basic", new EntityPattern.Builder(ModEntities.IM_CREEPER.get()).addTier(1, 1));

    EntityPattern IMP_T1 = register("imp_t1", new EntityPattern.Builder(ModEntities.IM_IMP.get()).addTier(1, 1));

    static EntityPattern register(String name, EntityPattern.Builder builder) {
        return register(name, builder, 0);
    }

    static EntityPattern register(String name, EntityPattern.Builder builder, float spawnWeight) {
        ResourceLocation id = ResourceLocation.fromNamespaceAndPath(InvasionMod.MODID, name);
        EntityPattern pattern = builder.build();
        REGISTRY.put(id, new PatternType(id, pattern, spawnWeight));
        return pattern;
    }

    static EntityPattern getPattern(ResourceLocation id) {
        return getKey(id).map(PatternType::pattern).orElse(EntityPatterns.ZOMBIE_T1_ANY);
    }

    static Optional<PatternType> getKey(ResourceLocation id) {
        return Optional.ofNullable(REGISTRY.get(id));
    }

    static boolean isPatternNameValid(ResourceLocation id) {
        return REGISTRY.containsKey(id);
    }

    record PatternType(ResourceLocation id, EntityPattern pattern, float defaultSpawnWeight) {
        public float getNightMobSpawnWeight() {
            return 0.f; // temp
            //return InvasionMod.getConfig().getPropertyValueFloat("nm-spawnpool1-slot-" + id + "-weight", defaultSpawnWeight);
        }
    }
}