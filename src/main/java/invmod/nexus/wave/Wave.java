package invmod.nexus.wave;


import invmod.nexus.spawns.Spawner;

import java.util.ArrayList;
import java.util.List;


public class Wave {
    private final List<WaveEntry> entries;
    private final int waveTotalTime;
    private final int waveBreakTime;

    private int elapsed;

    private Wave(int waveTotalTime, int waveBreakTime, List<WaveEntry> entries) {
        this.entries = entries;
        this.waveTotalTime = waveTotalTime;
        this.waveBreakTime = waveBreakTime;
    }

    public int doNextSpawns(int elapsedMillis, Spawner spawner) {
        int numberOfSpawns = 0;
        elapsed += elapsedMillis;
        for (WaveEntry entry : entries) {
            if (entry.getTime().isValueInRange(elapsed)) {
                numberOfSpawns += entry.doNextSpawns(elapsedMillis, spawner);
            }
        }
        return numberOfSpawns;
    }

    public int getTimeInWave() {
        return elapsed;
    }

    public int getWaveTotalTime() {
        return waveTotalTime;
    }

    public int getWaveBreakTime() {
        return waveBreakTime;
    }

    public boolean isComplete() {
        return elapsed > waveTotalTime;
    }

    public void resetWave() {
        this.elapsed = 0;
        for (WaveEntry entry : entries) {
            entry.resetToBeginning();
        }
    }

    public void setWaveToTime(int millis) {
        elapsed = millis;
    }

    public int getTotalMobAmount() {
        int total = 0;
        for (WaveEntry entry : entries) {
            total += entry.getAmount();
        }
        return total;
    }

    public static Builder builder(int duration, int restTime) {
        return new Builder(duration, restTime, new ArrayList<>());
    }

    public static final class Builder {
        private final int duration;
        private final int restTime;
        private final List<WaveEntry.Builder<?>> entries;

        public Builder(int duration, int restTime, List<WaveEntry.Builder<?>> entries) {
            this.duration = duration;
            this.restTime = restTime;
            this.entries = entries;
        }

        public Builder entry(WaveEntry.Builder<?> entry) {
            entries.add(entry);
            return this;
        }

        public Wave build() {
            return new Wave(duration, restTime, entries.stream().map(WaveEntry.Builder::build).toList());
        }
    }
}