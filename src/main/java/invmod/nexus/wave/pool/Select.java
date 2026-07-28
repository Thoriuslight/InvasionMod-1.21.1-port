package invmod.nexus.wave.pool;

import java.util.function.Consumer;

import net.minecraft.util.RandomSource;

public interface Select<T> {
    T selectNext(RandomSource random);

    default void reset() {

    }

    interface Builder<T> {
        Select<T> build();
    }

    interface PoolBuilder<T, K> extends Builder<T> {
        default PoolBuilder<T, K> entry(T entry, K amount) {
            return entry(() -> unary(entry), amount);
        }

        PoolBuilder<T, K> entry(Select.Builder<T> entry, K amount);

        default PoolBuilder<T, K> apply(Consumer<PoolBuilder<T, K>> consumer) {
            consumer.accept(this);
            return this;
        }
    }

    static <T> PoolBuilder<T, Integer> finite() {
        return new FiniteSelectionPool.Builder<>();
    }

    static <T> PoolBuilder<T, Float> random() {
        return new RandomSelectionPool.Builder<>();
    }

    static <T> Select<T> unary(T t) {
        return random -> t;
    }
}