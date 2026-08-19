package invmod.nexus.spawns;

import invmod.InvasionMod;
import invmod.util.math.PolarAngle;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.LevelAccessor;

public record SpawnPoint(BlockPos pos, int angle, SpawnType type) implements PolarAngle, Comparable<PolarAngle> {
    @Override
    public int getAngle() {
        return this.angle;
    }

    public void applyTo(Entity entity) {
        entity.moveTo(pos().getX() + 0.5, pos().getY() + 0.5, pos().getZ() + 0.5, angle, 0);
    }

    public boolean isValidFor(LevelAccessor level, Mob entity) {
        if (level.isOutsideBuildHeight(pos)) {
            InvasionMod.LOGGER.info("[Spawn] Spawn point was outside of build limit {}", pos);
            return false;
        }
        applyTo(entity);
        return entity.checkSpawnObstruction(level) && level.noCollision(entity);
    }

    public boolean trySpawnEntity(ServerLevel level, Mob entity) {
        if (isValidFor(level, entity)) {
            entity.finalizeSpawn(level, level.getCurrentDifficultyAt(entity.getOnPos()), MobSpawnType.STRUCTURE, null);
            level.addFreshEntityWithPassengers(entity);
            return true;
        }
        return false;
    }

    public boolean columnEquals(SpawnPoint position) {
        return pos().getX() == position.pos().getX() && pos.getZ() == position.pos().getZ();
    }

    @Override
    public int compareTo(PolarAngle polarAngle) {
        if (angle < polarAngle.getAngle()) {
            return -1;
        }
        if (angle > polarAngle.getAngle()) {
            return 1;
        }

        return 0;
    }
}