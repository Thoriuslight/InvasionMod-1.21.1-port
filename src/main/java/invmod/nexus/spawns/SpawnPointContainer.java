package invmod.nexus.spawns;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import invmod.nexus.wave.EntityPattern;
import invmod.util.math.PolarAngle;
import net.minecraft.util.InclusiveRange;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.Nullable;


public class SpawnPointContainer {
    private final Map<SpawnType, List<SpawnPoint>> spawnPoints = new EnumMap<>(SpawnType.class);
    private boolean sorted;
    private Random random = new Random();

    public void addSpawnPointXZ(SpawnPoint spawnPoint) {
        boolean foundMatch = false;
        List<SpawnPoint> spawnList = spawnPoints.computeIfAbsent(spawnPoint.type(), i -> new ArrayList<>());

        for (int i = 0; i < spawnList.size(); i++) {
            SpawnPoint oldPoint = spawnList.get(i);
            if (oldPoint.columnEquals(spawnPoint)) {
                if (oldPoint.pos().getY() > spawnPoint.pos().getY()) {
                    spawnList.set(i, spawnPoint);
                }
                foundMatch = true;
                break;
            }
        }

        if (!foundMatch) {
            spawnList.add(spawnPoint);
        }
        this.sorted = false;
    }

    @Nullable
    public SpawnPoint getRandomSpawnPoint(SpawnType spawnType) {
        List<SpawnPoint> spawnList = spawnPoints.getOrDefault(spawnType, List.of());
        return spawnList.isEmpty() ? null : spawnList.get(random.nextInt(spawnList.size()));
    }

    public SpawnPoint getRandomSpawnPoint(SpawnType spawnType, InclusiveRange<Integer> angle) {
        int minAngle = angle.minInclusive();
        int maxAngle = angle.maxInclusive();
        List<SpawnPoint> spawnList = spawnPoints.get(spawnType);
        if (spawnList.isEmpty()) {
            return null;
        }

        if (!this.sorted) {
            Collections.sort(spawnList);
            this.sorted = true;
        }

        int start = Collections.binarySearch(spawnList, PolarAngle.of(minAngle));
        if (start < 0) {
            start = -start - 1;
        }
        int end = Collections.binarySearch(spawnList, PolarAngle.of(maxAngle));
        if (end < 0) {
            end = -end - 1;
        }
        if (end > start) {
            return spawnList.get(start + this.random.nextInt(end - start));
        }
        if ((start > end) && (end > 0)) {
            int r = start + this.random.nextInt(spawnList.size() + end - start);
            if (r >= spawnList.size()) {
                r -= spawnList.size();
            }
            return spawnList.get(r);
        }
        return null;
    }

    public int getNumberOfSpawnPoints(SpawnType type) {
        return spawnPoints.getOrDefault(SpawnType.HUMANOID, List.of()).size();
    }

    public int getNumberOfSpawnPoints(SpawnType spawnType, InclusiveRange<Integer>  angle) {
        int minAngle = angle.minInclusive();
        int maxAngle = angle.maxInclusive();
        List<SpawnPoint> spawnList = spawnPoints.get(spawnType);
        if (spawnList.isEmpty() || (maxAngle - minAngle) >= 360) {
            return spawnList.size();
        }

        if (!sorted) {
            Collections.sort(spawnList);
            sorted = true;
        }

        int start = Collections.binarySearch(spawnList, PolarAngle.of(minAngle));
        if (start < 0) {
            start = -start - 1;
        }
        int end = Collections.binarySearch(spawnList, PolarAngle.of(maxAngle));
        if (end < 0) {
            end = -end - 1;
        }
        if (end > start) {
            return end - start;
        }
        if ((start > end) && (end > 0)) {
            return end + spawnList.size() - start;
        }
        return 0;
    }

    public void pointDisplayTest(Block block, Level level) {
        for (SpawnPoint point : spawnPoints.get(SpawnType.HUMANOID)) {
            level.setBlockAndUpdate(point.pos(), block.defaultBlockState());
        }
    }
}