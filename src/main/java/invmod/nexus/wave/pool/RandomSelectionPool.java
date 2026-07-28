package invmod.nexus.wave.pool;

import invmod.InvasionMod;
import net.minecraft.util.RandomSource;

import java.util.ArrayList;
import java.util.List;


record RandomSelectionPool<T>(List<Entry<T>> pool, float totalWeight) implements Select<T> {
    @Override
    public T selectNext(RandomSource random) {
        float r = random.nextFloat() * totalWeight;
        for (Entry<T> entry : pool) {
            if (r < entry.weight()) {
                return entry.value().selectNext(random);
            }

            r -= entry.weight();
        }

        if (!pool.isEmpty()) {
            InvasionMod.LOGGER.warn("RandomSelectionPool invalid setup or rounding error. Failing safe.");
            return pool.get(0).value().selectNext(random);
        }
        return null;
    }

    private record Entry<T>(Select<T> value, float weight) {
        public record Builder<T> (Select.Builder<T> value, float amount) {
            Entry<T> build() {
                return new Entry<>(value.build(), amount);
            }
        }
    }

    public static class Builder<T> implements Select.PoolBuilder<T, Float> {
        private final List<Entry.Builder<T>> entries = new ArrayList<>();
        private int total;

        @Override
        public Builder<T> entry(Select.Builder<T> entry, Float amount) {
            entries.add(new Entry.Builder<>(entry, amount));
            total += amount;
            return this;
        }

        @Override
        public Select<T> build() {
            return new RandomSelectionPool<>(entries.stream().map(Entry.Builder::build).toList(), total);
        }
    }
}