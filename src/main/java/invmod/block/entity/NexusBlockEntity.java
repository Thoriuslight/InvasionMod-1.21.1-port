package invmod.block.entity;

import invmod.InvasionMod;
import invmod.ModBlockEntities;
import invmod.ModEntities;
import invmod.block.NexusBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Port of the original {@code TileEntityNexus} state machine. Manages a
 * tower-defense wave spawner: bound to a player, spawns invasion mobs in a
 * configurable radius, tracks per-wave kill count, transitions through
 * spawning -> awaiting-clear -> cooldown -> next-wave.
 */
public final class NexusBlockEntity extends BlockEntity {

    /** Idle. Block visually "off". */
    public static final int MODE_IDLE       = 0;
    /** Currently spawning mobs for the current wave. */
    public static final int MODE_SPAWNING   = 1;
    /** All mobs spawned, waiting for arena clear (death of all spawned mobs). */
    public static final int MODE_AWAIT_CLEAR = 2;
    /** Wave cleared; cooling down before next wave starts. */
    public static final int MODE_COOLDOWN   = 3;

    private static final double SPAWN_TRY_PER_MOB    = 12;
    private static int spawnIntervalTicks() { return invmod.Config.SPAWN_INTERVAL_TICKS.get(); }
    private static int cooldownTicks()      { return invmod.Config.COOLDOWN_TICKS.get(); }

    private int mode = MODE_IDLE;
    private int waveNumber;
    private int mobsSpawnedThisWave;
    private int mobsTargetThisWave;
    private int spawnRadius = 32;

    private long lastSpawnTick;
    private long cooldownEndsAt;

    private @Nullable UUID boundPlayer;
    private @Nullable String boundPlayerName;

    private final List<UUID> trackedMobs = new ArrayList<>();

    public NexusBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.NEXUS.get(), pos, state);
    }

    /** Snapshot the live state for the {@link invmod.menu.NexusMenu} data slots. */
    public net.minecraft.world.inventory.ContainerData asContainerData() {
        return new net.minecraft.world.inventory.ContainerData() {
            @Override public int get(int idx) {
                return switch (idx) {
                    case 0 -> mode;
                    case 1 -> waveNumber;
                    case 2 -> mobsSpawnedThisWave;
                    case 3 -> mobsTargetThisWave;
                    case 4 -> spawnRadius;
                    default -> 0;
                };
            }
            @Override public void set(int idx, int value) { /* server-authoritative */ }
            @Override public int getCount() { return 5; }
        };
    }

    // ---- public API used by the block / GUI ------------------------------

    public int getMode()         { return mode; }
    public int getWaveNumber()   { return waveNumber; }
    public int getSpawnRadius()  { return spawnRadius; }
    public int getMobsSpawnedThisWave()  { return mobsSpawnedThisWave; }
    public int getMobsTargetThisWave()   { return mobsTargetThisWave; }
    public @Nullable UUID getBoundPlayerId() { return boundPlayer; }
    public String getBoundPlayerName() { return boundPlayerName == null ? "<none>" : boundPlayerName; }

    public void bindPlayer(Player player) {
        this.boundPlayer = player.getUUID();
        this.boundPlayerName = player.getGameProfile().getName();
        setChanged();
    }

    public void cycleSpawnRadius() {
        this.spawnRadius = switch (this.spawnRadius) {
            case 16 -> 32;
            case 32 -> 48;
            case 48 -> 64;
            default -> 16;
        };
        setChanged();
    }

    public void startWave(@Nullable Player triggering, ServerLevel level) {
        if (triggering != null && this.boundPlayer == null) {
            bindPlayer(triggering);
        }
        if (this.waveNumber == 0) this.waveNumber = 1;
        this.mode = MODE_SPAWNING;
        this.mobsSpawnedThisWave = 0;
        this.mobsTargetThisWave  = computeWaveSize(this.waveNumber);
        this.lastSpawnTick = level.getGameTime();
        this.trackedMobs.clear();
        setActiveState(level, true);
        announce(level, "Wave " + this.waveNumber + " — invasion starting (" + this.mobsTargetThisWave + " mobs)");
        setChanged();
    }

    public void stopWave(ServerLevel level) {
        if (this.mode == MODE_IDLE) return;
        this.mode = MODE_IDLE;
        this.mobsSpawnedThisWave = 0;
        this.mobsTargetThisWave  = 0;
        this.trackedMobs.clear();
        setActiveState(level, false);
        announce(level, "Invasion halted by damping agent.");
        setChanged();
    }

    // ---- ticker ----------------------------------------------------------

    public void serverTick(ServerLevel level) {
        switch (this.mode) {
            case MODE_SPAWNING -> tickSpawning(level);
            case MODE_AWAIT_CLEAR -> tickAwaitClear(level);
            case MODE_COOLDOWN -> tickCooldown(level);
            default -> { /* idle */ }
        }
    }

    private void tickSpawning(ServerLevel level) {
        if (level.getGameTime() - this.lastSpawnTick < spawnIntervalTicks()) return;
        this.lastSpawnTick = level.getGameTime();

        if (this.mobsSpawnedThisWave >= this.mobsTargetThisWave) {
            this.mode = MODE_AWAIT_CLEAR;
            announce(level, "Wave " + this.waveNumber + " — all mobs spawned. Survive!");
            setChanged();
            return;
        }

        EntityType<?> picked = pickMobForWave(this.waveNumber, level.random.nextFloat());
        if (trySpawn(level, picked)) {
            this.mobsSpawnedThisWave++;
            level.playSound(null, getBlockPos(),
                    SoundEvents.SOUL_ESCAPE.value(), SoundSource.HOSTILE, 0.6F, 0.7F);
            setChanged();
        }
    }

    private void tickAwaitClear(ServerLevel level) {
        // Sweep tracked mobs every 20 ticks
        if (level.getGameTime() % 20 != 0) return;
        this.trackedMobs.removeIf(id -> {
            Entity e = level.getEntity(id);
            return e == null || !e.isAlive();
        });
        if (this.trackedMobs.isEmpty()) {
            this.waveNumber++;
            this.mode = MODE_COOLDOWN;
            this.cooldownEndsAt = level.getGameTime() + cooldownTicks();
            announce(level, "Wave " + (this.waveNumber - 1) + " cleared! Next wave in "
                    + (cooldownTicks() / 20) + "s.");
            setChanged();
        }
    }

    private void tickCooldown(ServerLevel level) {
        if (level.getGameTime() >= this.cooldownEndsAt) {
            startWave(null, level);
        }
    }

    // ---- spawn helpers ---------------------------------------------------

    private boolean trySpawn(ServerLevel level, EntityType<?> type) {
        BlockPos origin = getBlockPos();
        for (int i = 0; i < SPAWN_TRY_PER_MOB; i++) {
            double ang = level.random.nextDouble() * Math.PI * 2.0;
            double dist = (this.spawnRadius * 0.4) + level.random.nextDouble() * (this.spawnRadius * 0.6);
            int x = origin.getX() + (int) (Math.cos(ang) * dist);
            int z = origin.getZ() + (int) (Math.sin(ang) * dist);
            int y = level.getHeight(net.minecraft.world.level.levelgen.Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, x, z);
            BlockPos at = new BlockPos(x, y, z);
            if (!level.isLoaded(at) || !level.noCollision(type.getDimensions().makeBoundingBox(x + 0.5, y, z + 0.5))) continue;
            Entity e = type.spawn(level, at, MobSpawnType.MOB_SUMMONED);
            if (e != null) {
                this.trackedMobs.add(e.getUUID());
                if (e instanceof net.minecraft.world.entity.Mob mob) {
                    mob.setPersistenceRequired();
                    // Stamp the Nexus position onto the mob so the GoToNexusGoal
                    // can read it back even after chunks unload + reload.
                    net.minecraft.nbt.ListTag nx = new net.minecraft.nbt.ListTag();
                    BlockPos np = getBlockPos();
                    nx.add(net.minecraft.nbt.IntTag.valueOf(np.getX()));
                    nx.add(net.minecraft.nbt.IntTag.valueOf(np.getY()));
                    nx.add(net.minecraft.nbt.IntTag.valueOf(np.getZ()));
                    mob.getPersistentData().put(invmod.entity.ai.GoToNexusGoal.NEXUS_POS_KEY, nx);

                    if (this.boundPlayer != null) {
                        Player target = level.getPlayerByUUID(this.boundPlayer);
                        if (target instanceof LivingEntity le) {
                            mob.setTarget(le);
                            mob.setLastHurtByMob(le);
                        }
                    }
                }
                return true;
            }
        }
        return false;
    }

    private static int computeWaveSize(int wave) {
        int base = invmod.Config.WAVE_SIZE_BASE.get();
        int linear = invmod.Config.WAVE_SIZE_LINEAR.get();
        int max = invmod.Config.MAX_WAVE_SIZE.get();
        return Math.min(max, base + wave * linear);
    }

    private static EntityType<?> pickMobForWave(int wave, float roll) {
        // Tier-gated pool: stronger mobs unlock at higher waves.
        if (wave >= 15 && roll < 0.10) return ModEntities.IM_GIANT_BIRD.get();
        if (wave >= 12 && roll < 0.18) return ModEntities.IM_BIRD.get();
        if (wave >= 10 && roll < 0.26) return ModEntities.IM_BURROWER.get();
        if (wave >= 8  && roll < 0.34) return ModEntities.IM_PIGMAN_ENGINEER.get();
        if (wave >= 8  && roll < 0.42) return ModEntities.IM_ZOMBIE_PIGMAN.get();
        if (wave >= 5  && roll < 0.55) return ModEntities.IM_THROWER.get();
        if (wave >= 3  && roll < 0.66) return ModEntities.IM_SPIDER.get();
        if (wave >= 3  && roll < 0.75) return ModEntities.IM_CREEPER.get();
        if (wave >= 2  && roll < 0.86) return ModEntities.IM_SKELETON.get();
        if (roll < 0.95) return ModEntities.IM_ZOMBIE.get();
        return ModEntities.IM_IMP.get();
    }

    private void setActiveState(ServerLevel level, boolean active) {
        BlockState st = level.getBlockState(getBlockPos());
        if (st.hasProperty(NexusBlock.ACTIVE)) {
            level.setBlock(getBlockPos(), st.setValue(NexusBlock.ACTIVE, active), 3);
        }
    }

    private void announce(ServerLevel level, String msg) {
        Component cmp = Component.literal("[Nexus] " + msg);
        if (this.boundPlayer != null) {
            Player p = level.getPlayerByUUID(this.boundPlayer);
            if (p != null) p.displayClientMessage(cmp, false);
        }
        InvasionMod.LOGGER.info("[Nexus@{}] {}", getBlockPos(), msg);
    }

    // ---- NBT --------------------------------------------------------------

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putInt("mode", mode);
        tag.putInt("wave", waveNumber);
        tag.putInt("radius", spawnRadius);
        tag.putInt("spawned", mobsSpawnedThisWave);
        tag.putInt("target", mobsTargetThisWave);
        tag.putLong("lastSpawnTick", lastSpawnTick);
        tag.putLong("cooldownEndsAt", cooldownEndsAt);
        if (boundPlayer != null) {
            tag.putUUID("boundPlayer", boundPlayer);
            tag.putString("boundPlayerName", boundPlayerName == null ? "" : boundPlayerName);
        }
        net.minecraft.nbt.ListTag list = new net.minecraft.nbt.ListTag();
        for (UUID id : trackedMobs) {
            CompoundTag t = new CompoundTag();
            t.putUUID("id", id);
            list.add(t);
        }
        tag.put("trackedMobs", list);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        this.mode = tag.getInt("mode");
        this.waveNumber = tag.getInt("wave");
        this.spawnRadius = tag.contains("radius") ? tag.getInt("radius") : 32;
        this.mobsSpawnedThisWave = tag.getInt("spawned");
        this.mobsTargetThisWave  = tag.getInt("target");
        this.lastSpawnTick = tag.getLong("lastSpawnTick");
        this.cooldownEndsAt = tag.getLong("cooldownEndsAt");
        this.boundPlayer = tag.hasUUID("boundPlayer") ? tag.getUUID("boundPlayer") : null;
        this.boundPlayerName = this.boundPlayer == null ? null : tag.getString("boundPlayerName");

        this.trackedMobs.clear();
        net.minecraft.nbt.ListTag list = tag.getList("trackedMobs", net.minecraft.nbt.Tag.TAG_COMPOUND);
        for (int i = 0; i < list.size(); i++) {
            CompoundTag t = list.getCompound(i);
            if (t.hasUUID("id")) this.trackedMobs.add(t.getUUID("id"));
        }
    }
}
