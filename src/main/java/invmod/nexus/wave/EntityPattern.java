package invmod.nexus.wave;


import invmod.nexus.EntityConstruct;
import invmod.nexus.wave.pool.Select;
import net.minecraft.util.InclusiveRange;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;

public record EntityPattern(
        Select<EntityType<? extends Mob>> typePool,
        Select<Integer> tierPool,
        Select<Integer> texturePool,
        Select<Integer> flavourPool) {
    public static final int MAX_ANGLE = 360;
    public static final int MAX_VALID_ANGLE = 180;

    private static final int DEFAULT_TIER = 1;
    private static final int DEFAULT_FLAVOUR = 0;
    private static final int OPEN_TEXTURE = 0;
    private static final int OPEN_SCALING = 0;

    public EntityConstruct generateEntityConstruct(RandomSource random) {
        return generateEntityConstruct(random, new InclusiveRange<>(-180, 180));
    }

    public EntityConstruct generateEntityConstruct(RandomSource random, InclusiveRange<Integer> angle) {
        EntityType<? extends Mob> type = typePool.selectNext(random);
        Integer tier = tierPool.selectNext(random);
        Integer texture = texturePool.selectNext(random);
        Integer flavour = flavourPool.selectNext(random);
        return new EntityConstruct(type,
                tier == null ? DEFAULT_TIER : tier,
                texture == null ? OPEN_TEXTURE : texture,
                flavour == null ? DEFAULT_FLAVOUR : flavour, OPEN_SCALING, angle.minInclusive(), angle.maxInclusive());
    }

    public static final class Builder {
        private final Select.PoolBuilder<EntityType<? extends Mob>, Float> typePool = Select.random();
        private final Select.PoolBuilder<Integer, Float> tierPool = Select.random();
        private final Select.PoolBuilder<Integer, Float> texturePool = Select.random();
        private final Select.PoolBuilder<Integer, Float> flavourPool = Select.random();

        public Builder(EntityType<? extends Mob> entityType) {
            addType(entityType, 1);
        }

        public Builder addType(EntityType<? extends Mob> entityType, float weight) {
            typePool.entry(entityType, weight);
            return this;
        }

        public Builder addTier(int tier, float weight) {
            tierPool.entry(tier, weight);
            return this;
        }

        public Builder addTexture(int texture, float weight) {
            texturePool.entry(texture, weight);
            return this;
        }

        public Builder addFlavour(int flavour, float weight) {
            flavourPool.entry(flavour, weight);
            return this;
        }

        public EntityPattern build() {
            return new EntityPattern(typePool.build(), tierPool.build(), texturePool.build(), flavourPool.build());
        }
    }
}