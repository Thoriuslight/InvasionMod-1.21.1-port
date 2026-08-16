package invmod.nexus.wave;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import invmod.nexus.wave.pool.Select;
import org.jetbrains.annotations.Nullable;

public class WaveBuilder {
    public static final int WAVES_DEFINED = 11;
    private static final float ZOMBIE_T1_WEIGHT = 1;
    private static final float ZOMBIE_T2_WEIGHT = 2;
    private static final float SPIDER_T1_WEIGHT = 1;
    private static final float SPIDER_T2_WEIGHT = 2;

    private Random rand = new Random();

    public Wave generateWave(float difficulty, final float tierLevel, int lengthSeconds) {
        float basicMobsPerSecond = 0.12F * difficulty;
        int numberOfGroups = 7;
        int numberOfBigGroups = 1;
        float proportionInGroups = 0.5F;
        int mobsPerGroup = Math.round(proportionInGroups * basicMobsPerSecond * lengthSeconds / (numberOfGroups + numberOfBigGroups * 2));
        int mobsPerBigGroup = mobsPerGroup * 2;
        int remainingMobs = (int) (basicMobsPerSecond * lengthSeconds) - mobsPerGroup * numberOfGroups - mobsPerBigGroup * numberOfBigGroups;
        int mobsPerSteady = Math.round(0.7F * remainingMobs / numberOfGroups);
        int extraMobsForFinale = Math.round(0.3F * remainingMobs);
        int extraMobsForCleanup = (int) (basicMobsPerSecond * lengthSeconds * 0.2F);
        float timeForGroups = 0.5F;
        int groupTimeInterval = (int) (lengthSeconds * 1000 * timeForGroups / (numberOfGroups + numberOfBigGroups * 3));
        int steadyTimeInterval = (int) (lengthSeconds * 1000 * (1 - timeForGroups) / numberOfGroups);

        int time = 0;

        var steadyPool = generateSteadyPool(tierLevel);
        var groupPool = generateGroupPool(tierLevel);

        List<WaveEntry.Builder<?>> entryList = new ArrayList<>();
        for (int i = 0; i < numberOfGroups; i++) {
            if (rand.nextInt(2) == 0) {
                entryList.add(WaveEntry.builder(groupPool).begin(time).end(time + 3500).amount(mobsPerGroup).granularity(500).angle(25).minSpawns(3));
                entryList.add(WaveEntry.builder(steadyPool).begin(time += groupTimeInterval).end(time += steadyTimeInterval).amount(mobsPerSteady).granularity(2000).angle(160).minSpawns(5));
            } else {
                entryList.add(WaveEntry.builder(steadyPool).begin(time).end(time += steadyTimeInterval).amount(mobsPerSteady).granularity(2000).angle(160).minSpawns(5));
                entryList.add(WaveEntry.builder(groupPool).begin(time).end(time + 5000).amount(mobsPerGroup).granularity(500).angle(25).minSpawns(3));
                time += groupTimeInterval;
            }
        }

        time += (int) (groupTimeInterval * 0.75D);

        int steadyPoolFinalBegin = time + 5000;
        int steadyPoolFinalEnd = (int) (time + groupTimeInterval * 2.25F);
        int steadyPoolCount = extraMobsForFinale / 2;

        var cleanupEntry = WaveEntry.builder(steadyPool).begin(steadyPoolFinalBegin).end(steadyPoolFinalEnd).amount(steadyPoolCount).granularity(500).angle(160).minSpawns(5);

        return new Wave.Builder(time + 16000, steadyPoolFinalEnd * 3, entryList)
                .entry(WaveEntry.finite()
                        .entry(EntityPatterns.THROWER_T1, mobsPerBigGroup / 5)
                        .entry(generateGroupPool(tierLevel + 0.5F, 6), mobsPerBigGroup)
                        .begin(time).end(time + 8000).amount(mobsPerBigGroup + mobsPerBigGroup / 7).granularity(500).angle(45).minSpawns(3).alert("invmod.message.wave.grouplarge", 0))
                .entry(cleanupEntry)
                .entry(cleanupEntry)
                .entry(cleanupEntry)
                .entry(WaveEntry.builder(steadyPool).begin(time + 15000).end((int) (time + 10000 + groupTimeInterval * 2.25F)).amount(extraMobsForCleanup).granularity(500))
                .build();
    }

    private Select.PoolBuilder<EntityPattern, Float> generateGroupPool(float tierLevel) {
        return generateGroupPool(tierLevel, 6);
    }

    private Select.PoolBuilder<EntityPattern, Float> generateGroupPool(float tierLevel, float weight) {
        float[] weights = new float[6];
        for (int i = 0; i < 6; i++) {
            if (tierLevel - i * 0.5F > 0) {
                weights[i] = (tierLevel - i <= 1 ? tierLevel - i * 0.5F : 1);
            }
        }
        return Select.<EntityPattern>random()
                .entry(Select.<EntityPattern>random()
                        .entry(Select.<EntityPattern>random()
                                .entry(EntityPatterns.ZOMBIE_T1_ANY, ZOMBIE_T1_WEIGHT * weights[0])
                                .entry(EntityPatterns.ZOMBIE_T2_ANY_BASIC, ZOMBIE_T2_WEIGHT * weights[2])
                                .entry(EntityPatterns.ZOMBIE_PIGMAN_T1_ANY, ZOMBIE_T1_WEIGHT * weights[3]), 3.1F)
                        .entry(Select.<EntityPattern>random()
                                .entry(EntityPatterns.SPIDER_T1_ANY, SPIDER_T1_WEIGHT * weights[0])
                                .entry(EntityPatterns.SPIDER_T2_ANY, SPIDER_T2_WEIGHT * weights[2]), 0.7F)
                        .entry(EntityPatterns.SKELETON_T1_ANY, 0.8F), weight * 0.8333333F)
                .entry(Select.<EntityPattern>random()
                        .entry(EntityPatterns.PIGMAN_ENGINEER_T1_ANY, 4F)
                        .entry(EntityPatterns.THROWER_T1, 1.1F * weights[4])
                        .entry(EntityPatterns.ZOMBIE_T3_ANY, 1.1F * weights[5])
                        .entry(EntityPatterns.CREEPER_T1_BASIC, 0.7F * weights[3]), weight * 0.1666667F);
    }

    private Select.PoolBuilder<EntityPattern, Float> generateSteadyPool(float tierLevel) {
        float[] weights = new float[6];
        for (int i = 0; i < weights.length; i++) {
            if (tierLevel - i * 0.5F > 0) {
                weights[i] = (tierLevel - i <= 1 ? tierLevel - i * 0.5F : 1);
            }
        }

        return Select.<EntityPattern>random()
                .entry(Select.<EntityPattern>random()
                        .entry(Select.<EntityPattern>random()
                                .entry(EntityPatterns.ZOMBIE_T1_ANY, ZOMBIE_T1_WEIGHT * weights[0])
                                .entry(EntityPatterns.ZOMBIE_T2_ANY_BASIC, ZOMBIE_T2_WEIGHT * weights[2])
                                .entry(EntityPatterns.ZOMBIE_PIGMAN_T1_ANY, ZOMBIE_T1_WEIGHT * weights[3]), 3.1F)
                        .entry(Select.<EntityPattern>random()
                                .entry(EntityPatterns.SPIDER_T1_ANY, SPIDER_T1_WEIGHT * weights[0])
                                .entry(EntityPatterns.SPIDER_T2_ANY, SPIDER_T2_WEIGHT * weights[2]), 0.7F)
                        .entry(EntityPatterns.SKELETON_T1_ANY, 0.8F), 9F)
                .entry(Select.<EntityPattern>random()
                        .entry(EntityPatterns.PIGMAN_ENGINEER_T1_ANY, 3F)
                        .entry(EntityPatterns.ZOMBIE_T3_ANY, 1.1F * weights[5])
                        .entry(EntityPatterns.CREEPER_T1_BASIC, 0.8F * weights[3]), 1F);
    }

    @Nullable
    public static Wave generateMainInvasionWave(int waveNumber) {
        if (waveNumber < Waves.WAVES.size()) {
            return Waves.WAVES.get(waveNumber).build();
        }
        return generateExtendedWave(waveNumber).build();
    }

    private static Wave.Builder generateExtendedWave(int waveNumber) {
        float mobScale = (float) Math.pow(1.090000033378601D, waveNumber - 11);
        float timeScale = 1 + (waveNumber - 11) * 0.04F;
        return Wave.builder((int) (timeScale * 120000), (int) (timeScale * 35000))
                .entry(WaveEntry.random()
                        .entry(EntityPatterns.ZOMBIE_T1_ANY, 1.5F)
                        .entry(EntityPatterns.ZOMBIE_T2_ANY_BASIC, 2.2F)
                        .entry(EntityPatterns.ZOMBIE_T3_ANY, 0.26F)
                        .entry(EntityPatterns.ZOMBIE_PIGMAN_T1_ANY, 0.8F)
                        .entry(EntityPatterns.ZOMBIE_PIGMAN_T2_ANY, 0.5F)
                        .entry(EntityPatterns.ZOMBIE_PIGMAN_T2_ANY, 0.054F)
                        .entry(EntityPatterns.SKELETON_T1_ANY, 0.7F)
                        .entry(EntityPatterns.THROWER_T1, 0.18F)
                        .entry(EntityPatterns.THROWER_T2, 0.054F)
                        .entry(EntityPatterns.CREEPER_T1_BASIC, 0.054F)
                        .entry(EntityPatterns.IMP_T1, 0.4F).end((int) (timeScale * 30000)).amount((int) (mobScale * 7)).granularity(2000).angle(45).minSpawns(5))
                .entry(WaveEntry.random()
                        .entry(EntityPatterns.SPIDER_T2_ANY, 2F)
                        .entry(EntityPatterns.PIGMAN_ENGINEER_T1_ANY, 1F).end((int) (timeScale * 90000)).amount((int) (mobScale * 3)).granularity(500))
                .entry(WaveEntry.random()
                        .entry(EntityPatterns.ZOMBIE_PIGMAN_T1_ANY, 1.5F)
                        .entry(EntityPatterns.ZOMBIE_PIGMAN_T2_ANY, 0.7F)
                        .entry(EntityPatterns.ZOMBIE_PIGMAN_T3_ANY, 0.35F)
                        .entry(EntityPatterns.ZOMBIE_T2_ANY_BASIC, 1.5F)
                        .entry(EntityPatterns.SPIDER_T2_ANY, 1F)
                        .entry(EntityPatterns.ZOMBIE_T1_ANY, 1F)
                        .entry(EntityPatterns.SKELETON_T1_ANY, 1F)
                        .entry(EntityPatterns.THROWER_T1, 0.5F)
                        .entry(EntityPatterns.THROWER_T2, 0.42F)
                        .entry(EntityPatterns.ZOMBIE_T3_ANY, 0.5F)
                        .entry(EntityPatterns.CREEPER_T1_BASIC, 0.42F)
                        .entry(EntityPatterns.IMP_T1, 0.4F).begin((int) (timeScale * 65000)).end((int) (timeScale * 67000)).amount((int) (mobScale * 7)).granularity(500).angle(25).minSpawns(3))
                .entry(WaveEntry.random()
                        .entry(EntityPatterns.ZOMBIE_T2_ANY_BASIC, 2F)
                        .entry(EntityPatterns.ZOMBIE_T1_ANY, 3F)
                        .entry(EntityPatterns.SPIDER_T3_ANY, 1F).begin((int) (timeScale * 95000)).end((int) (timeScale * 97000)).amount((int) (mobScale * 6)).granularity(500).angle(45).minSpawns(2));
    }
}