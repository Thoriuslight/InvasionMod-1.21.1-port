package invmod.nexus.spawns;


import invmod.nexus.EntityConstruct;
import net.minecraft.ChatFormatting;
import net.minecraft.util.InclusiveRange;
import net.minecraft.util.RandomSource;

public interface Spawner {
    RandomSource getRandom();

    boolean attemptSpawn(EntityConstruct mobConstruct, InclusiveRange<Integer> angle);

    int getNumberOfPointsInRange(InclusiveRange<Integer> angle, SpawnType type);

    void sendSpawnAlert(String message, ChatFormatting color);

    void noSpawnPointNotice();
}