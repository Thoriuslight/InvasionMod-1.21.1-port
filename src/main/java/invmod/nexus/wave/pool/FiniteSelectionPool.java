package invmod.nexus.wave.pool;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.util.RandomSource;
import org.jetbrains.annotations.Nullable;


class FiniteSelectionPool<T> implements Select<T> {
    private final List<Entry<T>> currentPool;
    private final int originalAmount;
    private int totalAmount;

    private FiniteSelectionPool(int totalAmount, List<Entry<T>> entries) {
        this.totalAmount = totalAmount;
        this.originalAmount = totalAmount;
        this.currentPool = entries;
    }

    @Nullable
    @Override
    public T selectNext(RandomSource random) {
        if (totalAmount < 1) {
            reset();
        }
        float r = random.nextInt(totalAmount);
        for (Entry<T> entry : currentPool) {
            if (r < entry.amount) {
                totalAmount--;
                return entry.selectNext(random);
            }

            r -= entry.amount;
        }

        return null;
    }

    @Override
    public void reset() {
        totalAmount = originalAmount;
        currentPool.forEach(Select::reset);
    }

    @Override
    public String toString() {
        String s = "FiniteSelectionPool@" + Integer.toHexString(hashCode()) + "#Size=" + currentPool.size();
        for (int i = 0; i < currentPool.size(); i++) {
            s = s + "\n\tEntry " + i + "   Amount: " + currentPool.get(i).initialAmount;
            s = s + "\n\t" + currentPool.get(i).amount;
        }
        return s;
    }

    private static class Entry<T> implements Select<T> {
        Select<T> value;
        int amount;
        final int initialAmount;

        Entry(Select<T> value, int amount) {
            this.value = value;
            this.initialAmount = amount;
            this.amount = amount;
        }

        @Override
        public T selectNext(RandomSource random) {
            amount--;
            return value.selectNext(random);
        }

        @Override
        public void reset() {
            amount = initialAmount;
            value.reset();
        }

        public record Builder<T> (Select.Builder<T> value, int amount) {
            Entry<T> build() {
                return new Entry<>(value.build(), amount);
            }
        }
    }

    public static class Builder<T> implements Select.PoolBuilder<T, Integer> {
        private final List<Entry.Builder<T>> entries = new ArrayList<>();
        private int total;

        @Override
        public Builder<T> entry(Select.Builder<T> entry, Integer amount) {
            entries.add(new Entry.Builder<>(entry, amount));
            total += amount;
            return this;
        }

        @Override
        public Select<T> build() {
            return new FiniteSelectionPool<>(total, entries.stream().map(Entry.Builder::build).toList());
        }
    }
}