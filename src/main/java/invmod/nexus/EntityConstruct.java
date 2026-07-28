package invmod.nexus;


import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;

public record EntityConstruct (
        EntityType<? extends Mob> entityType,
        int texture,
        int tier,
        int flavour,
        float scaling,
        int minAngle,
        int maxAngle
) {

    public Mob createMob(NexusAccess nexus) {
        return createMob(nexus.getWorld(), nexus);
    }

    public Mob createMob(Level level, @Nullable NexusAccess nexus) {
        Mob entity = entityType().create(level);
        if (entity instanceof BuildableMob b) {
            b.onSpawned(nexus, this);
        }
        return entity;
    }

    public Mob createMob(ServerLevel world, @Nullable NexusAccess nexus, BlockPos position) {
        return entityType().create(world, entity -> {
            if (entity instanceof BuildableMob b) {
                b.onSpawned(nexus, this);
            }
        }, position, MobSpawnType.NATURAL, true, false);
    }

    public interface BuildableMob {
        void onSpawned(NexusAccess nexus, EntityConstruct spawnConditions);
    }
}