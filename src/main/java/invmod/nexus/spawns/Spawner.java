package invmod.nexus.spawns;


import invmod.nexus.EntityConstruct;
import net.minecraft.network.chat.Style;
import net.minecraft.util.InclusiveRange;
import net.minecraft.util.RandomSource;

public interface Spawner {
    RandomSource getRandom();

    boolean attemptSpawn(EntityConstruct mobConstruct, InclusiveRange<Integer> angle);

    int getNumberOfPointsInRange(InclusiveRange<Integer> angle, SpawnType type);

    void sendSpawnAlert(String message, Style color);

    void noSpawnPointNotice();
}