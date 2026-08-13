package invmod.block.entity;

import invmod.InvasionMod;
import invmod.ModEntities;
import invmod.block.NexusBlock;
import invmod.menu.NexusMenu;
import invmod.nexus.NexusAccess;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
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
public final class NexusBlockEntity extends BlockEntity implements MenuProvider {
    private static final int[] SLOTS = {0, 1};

    private UUID nexusId = UUID.randomUUID();
    @Nullable
    private Nexus nexus;

    public NexusBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.NEXUS.get(), pos, state);
    }

    public NexusAccess getNexus() {
        if (nexus == null && getLevel() instanceof ServerLevel sw) {
            nexus = WorldNexusStorage.of(sw).getOrCreate(nexusId, getBlockPos());
        }
        return nexus;
    }

    @Override
    public void setLevel(Level level) {
        super.setLevel(level);
        if (nexus != null && nexus.getLevel() != level) {
            nexus = null;
        }
    }

    @Override
    public void setStack(int i, ItemStack stack) {
        if (getNexus() != null) {
            nexus.getHeldItems().setStack(i, stack);
        }
    }

    @Override
    public ItemStack getStack(int i) {
        return getNexus() != null ? nexus.getHeldItems().getStack(i) : ItemStack.EMPTY;
    }

    @Override
    public ItemStack removeStack(int slot, int amount) {
        return getNexus() != null ? nexus.getHeldItems().removeStack(slot, amount) : ItemStack.EMPTY;
    }

    @Override
    public boolean canPlayerUse(PlayerEntity entityplayer) {
        return true;
    }

    @Override
    public boolean isEmpty() {
        return getNexus() != null && nexus.getHeldItems().isEmpty();
    }

    @Override
    public ItemStack removeStack(int slot) {
        return getNexus() != null ? nexus.getHeldItems().removeStack(slot) : ItemStack.EMPTY;
    }

    @Override
    public void clear() {
        if (getNexus() != null) {
            nexus.getHeldItems().clear();
        }
    }

    @Override
    public int size() {
        return getNexus() != null ? nexus.getHeldItems().size() : 0;
    }

    @Override
    public int[] getAvailableSlots(Direction side) {
        return SLOTS;
    }

    @Override
    public boolean canInsert(int slot, ItemStack stack, Direction dir) {
        return false;
    }

    @Override
    public boolean canExtract(int slot, ItemStack stack, Direction dir) {
        return true;
    }

    public void tick(ServerLevel world, BlockPos pos, BlockState state) {

    }

    public void discard() {
        if (getLevel() instanceof ServerLevel sl) {
            WorldNexusStorage.of(sl).destroyNexus(nexusId);
        }
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        if (getNexus() == null) {
            return null;
        }
        return new NexusMenu(containerId, playerInventory, this, nexus.getProperties(), ScreenHandlerContext.create(player.getWorld(), getBlockPos()));
    }

    @Override
    public Component getDisplayName() {
        return getBlockState().getBlock().getName();
    }

    @Override
    protected void readNbt(NbtCompound compound, RegistryWrapper.WrapperLookup lookup) {
        super.readNbt(compound, lookup);
        nexusId = compound.getUuid("nexusId");
        nexus = null;
    }

    @Override
    public void writeNbt(NbtCompound compound, RegistryWrapper.WrapperLookup lookup) {
        super.writeNbt(compound, lookup);
        compound.putUuid("nexusId", nexusId);
    }
    /*

    private static final double SPAWN_TRY_PER_MOB    = 12;
    private static int spawnIntervalTicks() { return invmod.Config.SPAWN_INTERVAL_TICKS.get(); }
    private static int cooldownTicks()      { return invmod.Config.COOLDOWN_TICKS.get(); }

    private final List<UUID> trackedMobs = new ArrayList<>();

    public NexusBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.NEXUS.get(), pos, state);
    }


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
            @Override public void set(int idx, int value) {  }
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

    public void tick(ServerLevel level, BlockPos pos, BlockState state) {
        switch (this.mode) {
            case MODE_SPAWNING -> tickSpawning(level);
            case MODE_AWAIT_CLEAR -> tickAwaitClear(level);
            case MODE_COOLDOWN -> tickCooldown(level);
            default -> {  }
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
*/
}
