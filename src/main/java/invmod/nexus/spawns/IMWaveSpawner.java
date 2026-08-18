package invmod.nexus.spawns;



import java.util.ArrayList;
import java.util.List;

import invmod.InvasionMod;
import invmod.ModEntities;
import invmod.entity.IMZombieEntity;
import invmod.nexus.Combatant;
import invmod.nexus.EntityConstruct;
import invmod.nexus.NexusAccess;
import invmod.nexus.wave.Wave;
import invmod.nexus.wave.WaveBuilder;
import invmod.nexus.wave.WaveSpawnerException;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Style;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.InclusiveRange;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Mob;
import org.jetbrains.annotations.Nullable;



public class IMWaveSpawner implements Spawner {
    private static final int MAX_SPAWN_TRIES = 20;
    public static final int MIN_SPAWN_RADIUS = 8;
    private static final int NORMAL_SPAWN_HEIGHT = 30;
    private static final int MIN_SPAWN_POINTS_TO_KEEP = 15;
    private static final int MIN_SPAWN_POINTS_TO_KEEP_BELOW_HEIGHT_CUTOFF = 20;
    private static final int HEIGHT_CUTOFF = 35;
    private static final float SPAWN_POINT_CULL_RATE = 0.3F;

    private SpawnPointContainer spawnPointContainer = new SpawnPointContainer();

    private final NexusAccess nexus;

    @Nullable
    private Wave currentWave;

    private boolean active;
    private boolean waveComplete;
    private boolean permitSpawns = true;
    private boolean debugMode;

    private int spawnRadius;
    private int successfulSpawns;
    private long elapsed;

    public IMWaveSpawner(NexusAccess nexus, int radius) {
        this.nexus = nexus;
        this.spawnRadius = radius;
    }

    @Override
    public RandomSource getRandom() {
        return nexus.getWorld().getRandom();
    }

    public long getElapsedTime() {
        return elapsed;
    }

    public boolean setRadius(int radius) {
        radius = Math.max(8, radius);
        spawnRadius = radius;
        return spawnRadius != radius;
    }

    public int getRadius() {
        return spawnRadius;
    }

    public void beginNextWave(int waveNumber) throws WaveSpawnerException {
        beginNextWave(WaveBuilder.generateMainInvasionWave(waveNumber));
    }

    public void beginNextWave(Wave wave) throws WaveSpawnerException {
        if (!active) {
            generateSpawnPoints();
        } else if (debugMode) {
            InvasionMod.LOGGER.info("Successful spawns of last wave: " + successfulSpawns);
        }

        wave.resetWave();
        waveComplete = false;
        active = true;
        currentWave = wave;
        elapsed = 0L;
        successfulSpawns = 0;

        if (debugMode) {
            InvasionMod.LOGGER.info("Defined mobs this wave: " + getTotalDefinedMobsThisWave());
        }
    }

    public void spawn(int elapsedMillis) throws WaveSpawnerException {
        elapsed += elapsedMillis;
        if (waveComplete || !active) {
            return;
        }

        if (spawnPointContainer.getNumberOfSpawnPoints(SpawnType.HUMANOID) < 10) {
            generateSpawnPoints();
            if (spawnPointContainer.getNumberOfSpawnPoints(SpawnType.HUMANOID) < 10) {
                throw new WaveSpawnerException("Not enough spawn points for type " + SpawnType.HUMANOID);
            }
        }
        currentWave.doNextSpawns(elapsedMillis, this);
        if (currentWave.isComplete()) {
            waveComplete = true;
        }
    }

    public int resumeFromState(Wave wave) throws WaveSpawnerException {
        stop();
        beginNextWave(wave);
        setPermitSpawns(false);
        int numberOfSpawns = 0;
        for (long i = 0; i < elapsed; i += 100L) {
            numberOfSpawns += currentWave.doNextSpawns(100, this);
        }
        setPermitSpawns(true);
        return numberOfSpawns;
    }

    public int resumeFromState(int waveNumber) throws WaveSpawnerException {
        stop();
        beginNextWave(waveNumber);
        setPermitSpawns(false);
        int numberOfSpawns = 0;
        for (long i = 0; i < elapsed; i += 100L) {
            numberOfSpawns += currentWave.doNextSpawns(100, this);
        }
        setPermitSpawns(true);
        return numberOfSpawns;
    }

    public void stop() {
        active = false;
    }

    public boolean isActive() {
        return active;
    }

    public boolean isReady() {
        return !active && nexus != null && nexus.getWorld() != null;
    }

    public boolean isWaveComplete() {
        return waveComplete;
    }

    public int getWaveDuration() {
        return currentWave.getWaveTotalTime();
    }

    public int getWaveRestTime() {
        return currentWave.getWaveBreakTime();
    }

    public int getSuccessfulSpawnsThisWave() {
        return successfulSpawns;
    }

    public int getTotalDefinedMobsThisWave() {
        return currentWave.getTotalMobAmount();
    }

    public void askForRespawn(Combatant<?> entity) {
        if (spawnPointContainer.getNumberOfSpawnPoints(SpawnType.HUMANOID) > 10) {
            SpawnPoint spawnPoint = spawnPointContainer.getRandomSpawnPoint(SpawnType.HUMANOID);
            if (spawnPoint != null) {
                final byte statusAddDeathParticles = (byte)60;
                spawnPoint.applyTo(entity.asEntity());
                entity.resetHealth();
                entity.asEntity().level().broadcastEntityEvent(entity.asEntity(), statusAddDeathParticles);
            }
        }
    }

    @Override
    public void sendSpawnAlert(String message, ChatFormatting color) {
        if (debugMode) {
            InvasionMod.LOGGER.info(message);
        }
        nexus.getParticipants().sendMessage(color, message);
    }

    @Override
    public void noSpawnPointNotice() {
    }

    public void debugMode(boolean isOn) {
        debugMode = isOn;
    }

    @Override
    public int getNumberOfPointsInRange(InclusiveRange<Integer> angle, SpawnType type) {
        return spawnPointContainer.getNumberOfSpawnPoints(type, angle);
    }

    public void setPermitSpawns(boolean flag) {
        permitSpawns = flag;
    }

    public void giveSpawnPoints(SpawnPointContainer spawnPointContainer) {
        this.spawnPointContainer = spawnPointContainer;
    }

    @Override
    public boolean attemptSpawn(EntityConstruct mobConstruct, InclusiveRange<Integer> angle) {
        if (!permitSpawns) {
            return false;
        }

        Mob mob = mobConstruct.createMob(nexus);
        int spawnTries = Math.min(spawnPointContainer.getNumberOfSpawnPoints(SpawnType.HUMANOID, angle), MAX_SPAWN_TRIES);

        for (int j = 0; j < spawnTries; j++) {
            @Nullable
            final SpawnPoint spawnPoint = angle.maxInclusive() - angle.minInclusive() >= 360
                    ? spawnPointContainer.getRandomSpawnPoint(SpawnType.HUMANOID)
                    : spawnPointContainer.getRandomSpawnPoint(SpawnType.HUMANOID, angle);

            if (spawnPoint == null) {
                return false;
            }
            if (!permitSpawns) {
                successfulSpawns++;
                if (debugMode) {
                    InvasionMod.LOGGER.info("[Spawn] Time: " + currentWave.getTimeInWave() / 1000 + "  Type: " + mob + "  Coords: " + spawnPoint + "  Specified: " + angle);
                }

                return true;
            }
            if (spawnPoint.trySpawnEntity((ServerLevel)nexus.getWorld(), mob)) {
                successfulSpawns++;
                System.out.println(successfulSpawns);
                if (debugMode) {
                    InvasionMod.LOGGER.info("[Spawn] Time: " + currentWave.getTimeInWave() / 1000 + "  Type: " + mob + "  Coords: " + mob.getX() + ", " + mob.getY() + ", " + mob.getZ() + "  θ" + spawnPoint.getAngle() + "  Specified: " + angle);
                }

                return true;
            }
        }
        InvasionMod.LOGGER.error("Could not find valid spawn for '" + mob.getName().getString() + "' after " + spawnTries + " tries");
        return false;
    }

    private void generateSpawnPoints() {
        IMZombieEntity zombie = ModEntities.IM_ZOMBIE.get().create(nexus.getWorld());
        //zombie.setNexus(nexus);
        List<SpawnPoint> spawnPoints = new ArrayList<>();
        BlockPos origin = nexus.getOrigin();
        BlockPos.MutableBlockPos mutable = origin.mutable();

        for (int vertical = 0;
             Math.abs(vertical) < spawnRadius && !nexus.getWorld().isOutsideBuildHeight(origin.getY() + vertical - 1);
             vertical = vertical > 0 ? vertical * -1 : vertical * -1 + 1) {
            for (int i = 0; i <= spawnRadius * 0.7D + 1; i++) {
                int j = (int) Math.round(spawnRadius * Math.cos(Math.asin(i / spawnRadius)));
                System.out.println("vertical");
                System.out.println(vertical);
                System.out.println(mutable.getY());
                System.out.println(mutable.set(origin).move( i, vertical, j).getY());
                addValidSpawn(zombie, spawnPoints, mutable.set(origin).move( i, vertical, j));
                addValidSpawn(zombie, spawnPoints, mutable.set(origin).move( i, vertical,-j));
                addValidSpawn(zombie, spawnPoints, mutable.set(origin).move(-i, vertical, j));
                addValidSpawn(zombie, spawnPoints, mutable.set(origin).move(-i, vertical,-j));

                addValidSpawn(zombie, spawnPoints, mutable.set(origin).move( j, vertical, i));
                addValidSpawn(zombie, spawnPoints, mutable.set(origin).move( j, vertical,-i));
                addValidSpawn(zombie, spawnPoints, mutable.set(origin).move(-j, vertical, i));
                addValidSpawn(zombie, spawnPoints, mutable.set(origin).move(-j, vertical,-i));
            }
        }

        if (spawnPoints.size() > MIN_SPAWN_POINTS_TO_KEEP) {
            int i;
            int amountToRemove = (int) ((spawnPoints.size() - MIN_SPAWN_POINTS_TO_KEEP) * SPAWN_POINT_CULL_RATE);
            for (i = spawnPoints.size() - 1; i >= spawnPoints.size() - amountToRemove; i--) {
                if (Math.abs(spawnPoints.get(i).pos().getY() - origin.getY()) < NORMAL_SPAWN_HEIGHT) {
                    break;
                }
            }
            for (; i >= MIN_SPAWN_POINTS_TO_KEEP_BELOW_HEIGHT_CUTOFF; i--) {
                SpawnPoint spawnPoint = spawnPoints.get(i);
                if (spawnPoint.pos().getY() - origin.getY() <= HEIGHT_CUTOFF) {
                    spawnPointContainer.addSpawnPointXZ(spawnPoint);
                }

            }
            for (; i >= 0; i--) {
                spawnPointContainer.addSpawnPointXZ(spawnPoints.get(i));
            }

        }

        InvasionMod.LOGGER.info("Found {} spawn points for next nexus wave", spawnPointContainer.getNumberOfSpawnPoints(SpawnType.HUMANOID));
    }

    private void addValidSpawn(Mob entity, List<SpawnPoint> spawnPoints, BlockPos pos) {
        if (nexus.getWorld().isOutsideBuildHeight(pos)) {
            InvasionMod.LOGGER.info("[Spawn] Spawn point was outside of build limit {}", pos);
            return;
        }
        entity.moveTo(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 0, 0);
        if (entity.checkSpawnObstruction(nexus.getWorld()) && nexus.getWorld().isUnobstructed(entity)) {
            int angle = (int) (Math.atan2(nexus.getOrigin().getZ() - pos.getZ(), nexus.getOrigin().getX() - pos.getX()) * Mth.RAD_TO_DEG);
            spawnPoints.add(new SpawnPoint(pos.immutable(), angle, SpawnType.HUMANOID));
        }
    }

    public void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        setRadius(tag.getInt("spawnRadius"));
        elapsed = tag.getLong("elapsed");
    }

    public CompoundTag saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        tag.putInt("spawnRadius", spawnRadius);
        tag.putLong("elapsed", elapsed);
        return tag;
    }
}