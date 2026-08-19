package invmod.nexus;


import invmod.InvasionConfig;
import invmod.InvasionMod;
import invmod.block.ModBlocks;
import invmod.block.NexusBlock;
import invmod.item.ModItems;
import invmod.nexus.ai.AttackerAI;
import invmod.nexus.spawns.IMWaveSpawner;
import invmod.nexus.wave.Wave;
import invmod.nexus.wave.WaveBuilder;
import invmod.nexus.wave.WaveSpawnerException;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.Container;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

import java.util.List;
import java.util.UUID;

public class Nexus implements ControllableNexusAccess {
    private static final int INITIAL_SPAWN_RADIUS = 52;
    private static final int MAX_POWER_LEVEL = 2200;
    private static final int MAX_ACTIVAION_TIME = 400;
    private static final int MAX_HEALTH = 100;

    private int activationTimer;

    private int currentWave;
    private int nexusLevel = 1;
    private int nexusKills;

    private int hp = MAX_HEALTH;
    private int lastHp = MAX_HEALTH;

    private Mode mode = Mode.STOPPED;
    private int powerLevel;

    private int lastPowerLevel;
    private int powerLevelTimer;

    private int mobsLeftInWave;
    private int lastMobsLeftInWave;

    private int mobsToKillInWave;

    private int nextAttackTime;

    private int daysToAttack;

    private long lastWorldTime;

    private int zapTimer;

    private int tickCount;

    private long waveDelayTimer;
    private long waveDelay;

    private boolean continuousAttack;

    private boolean activated;
    private boolean discarded;

    private final IMWaveSpawner waveSpawner = new IMWaveSpawner(this, INITIAL_SPAWN_RADIUS);
    private final WaveBuilder waveBuilder = new WaveBuilder();
    private final NexusInventory nexusItemStacks = new NexusInventory();

    private final Participants boundPlayers = new Participants(this);
    private final Combatants mobList;
    private final AttackerAI attackerAI = new AttackerAI(this);

    private final InvasionConfig config = InvasionConfig.CONFIG;

    private AABB boundingBoxToRadius;

    private BlockPos pos;
    private UUID uuid;

    private final ServerLevel level;
    private final WorldNexusStorage storage;

    private final ContainerData properties = new ContainerData() {
        @Override
        public int get(int index) {
            return switch (index) {
                case 0 -> activationTimer;
                case 1 -> getMode().ordinal();
                case 2 -> getCurrentWave();
                case 3 -> nexusLevel;
                case 4 -> nexusKills;
                case 5 -> getSpawnRadius();
                case 6 -> nexusItemStacks.getFluxProgress();
                case 7 -> powerLevel;
                case 8 -> nexusItemStacks.getCookTime();
                case 9 -> isActivating() ? 1 : 0;
                default -> 0;
            };
        }

        @Override
        public void set(int index, int j) {
            if (index == 0) {
                activationTimer = j;
            } else if (index == 1) {
                setMode(Mode.forId(j));
            } else if (index == 2) {
                currentWave = j;
            } else if (index == 3) {
                nexusLevel = j;
            } else if (index == 4) {
                nexusKills = j;
            } else if (index == 5) {
                setSpawnRadius(j);
            } else if (index == 6) {
                nexusItemStacks.setFlugProgress(j);
            } else if (index == 7) {
                powerLevel = j;
            } else if (index == 8) {
                nexusItemStacks.setCookTime(j);
            }
        }

        @Override
        public int getCount() {
            return 10;
        }
    };

    Nexus(ServerLevel level, WorldNexusStorage storage, UUID id, BlockPos pos) {
        this.uuid = id;
        this.level = level;
        this.storage = storage;
        this.pos = pos;
        mobList = new Combatants(this);
        boundingBoxToRadius = computeSpawnArea();
        nexusItemStacks.addListener(i -> storage.setDirty());
    }

    @Override
    public UUID getUuid() {
        return uuid;
    }

    @Override
    public boolean isDiscarded() {
        return discarded;
    }

    void discard() {
        discarded = true;
    }

    public Container getHeldItems() {
        return nexusItemStacks;
    }

    public ContainerData getProperties() {
        return properties;
    }

    @Override
    public Participants getParticipants() {
        return boundPlayers;
    }

    private AABB computeSpawnArea() {
        return new AABB(pos).inflate(getSpawnRadius() + 10, getSpawnRadius() + 40, getSpawnRadius() + 10);
    }

    private AABB getChunkBox(Level level) {
        return new AABB(pos).inflate(getSpawnRadius() + 10, getSpawnRadius() + 40, getSpawnRadius() + 10).setMinY(level.getMinBuildHeight()).setMaxY(level.getMaxBuildHeight());
    }

    @Override
    public boolean isActive() {
        return activated;
    }

    @Override
    public boolean isActivating() {
        return activationTimer > 0 && activationTimer < MAX_ACTIVAION_TIME;
    }

    @Override
    public Mode getMode() {
        return mode;
    }

    @Override
    public int getLevel() {
        return nexusLevel;
    }

    @Override
    public int getSpawnRadius() {
        return waveSpawner.getRadius();
    }

    @Override
    public Level getWorld() {
        return level;
    }

    @Override
    public BlockPos getOrigin() {
        return pos;
    }

    public Combatants getCombatants() {
        return mobList;
    }

    @Override
    public AttackerAI getAttackerAI() {
        return attackerAI;
    }

    @Override
    public int getCurrentWave() {
        return currentWave;
    }

    public void tick() {
        if (!mode.isActive()) {
            return;
        }
        try {
            tickCount = (tickCount + 1) % 60;
            if (tickCount == 0) {
                boundPlayers.bindPlayers(boundingBoxToRadius);
                mobList.updateMobList(boundingBoxToRadius);
            }

            if (mode == Mode.STARTED || mode == Mode.WAITING) {
                doInvasion(50);
            } else if (mode == Mode.CONTINUOUS) {
                doContinuous(50);
            }
            storage.setActiveNexus(this);
        } catch (WaveSpawnerException e) {
            InvasionMod.LOGGER.error("Exception occured whilst updating invasion", e);
            stop(false);
        }
    }

    public void onLoaded() {
        if (!mode.isActive()) {
            return;
        }
        boundingBoxToRadius = getChunkBox(level);
        if (mode == Mode.CONTINUOUS && continuousAttack) {
            if (resumeSpawnerContinuous()) {
                mobsLeftInWave = (lastMobsLeftInWave += acquireEntities());
            }
        } else {
            resumeSpawnerInvasion();
        }
    }

    @Override
    public void stop(boolean killEnemies) {
        if (mode == Mode.WAITING) {
            setMode(Mode.CONTINUOUS);
            int days = getWorld().getRandom().nextIntBetweenInclusive(config.minContinuousModeDays, config.maxContinuousModeDays);
            nextAttackTime = (int) ((getWorld().getGameTime() / TICKS_PER_DAY * TICKS_PER_DAY) + HALF_DAY_TIME + days * TICKS_PER_DAY);
        } else {
            setMode(Mode.STOPPED);
        }

        waveSpawner.stop();
        activationTimer = 0;
        currentWave = 0;
        activated = false;

        if (killEnemies) {
            killAllMobs();
        }
    }

    @Override
    public List<Component> getStatus() {
        return List.of(
                Component.literal("Current Time: " + getWorld().getGameTime()),
                Component.literal("Time to next: " + nextAttackTime),
                Component.literal("Days to attack: " + daysToAttack),
                Component.literal("Mobs left: " + mobsLeftInWave),
                Component.literal("Mode: " + mode)
        );
    }

    @Override
    public boolean setSpawnRadius(int radius) {
        if (!waveSpawner.isActive() && waveSpawner.setRadius(radius)) {
            boundingBoxToRadius = getChunkBox(getWorld());
            return true;
        }

        return false;
    }

    @Override
    public void damage(DamageSource source, int amount) {
        hp -= amount;
        if (hp <= 0) {
            hp = 0;
            if (mode == Mode.STARTED) {
                theEnd();
                //SpawnProxyEntity mob = InvEntities.SPAWN_PROXY.create(getWorld());
                //mob.setCustomName(ModBlocks.NEXUS.get().getName());
                //boundPlayers.sendMessage(source.getLocalizedDeathMessage(mob));
                boundPlayers.playSoundForBoundPlayers(SoundEvents.BLAZE_HURT);
            }
        }
        while (hp + 5 <= lastHp) {
            boundPlayers.sendMessage(ChatFormatting.DARK_RED, "invmod.message.nexus.hpat", (lastHp - 5));
            lastHp -= 5;
            boundPlayers.playSoundForBoundPlayers(SoundEvents.BLAZE_HURT);
        }
    }

    @Override
    public void notifyCombatantRemoved(Combatant<?> combatant, Entity.RemovalReason reason) {
        if (reason == Entity.RemovalReason.KILLED) {
            nexusKills++;
            mobsLeftInWave--;
            if (mobsLeftInWave <= 0) {
                if (lastMobsLeftInWave > 0) {
                    boundPlayers.sendMessage(ChatFormatting.GREEN, "invmod.message.nexus.stableagain");
                    boundPlayers.sendMessage(ChatFormatting.GREEN, "invmod.message.nexus.unleashingenergy");
                    lastMobsLeftInWave = mobsLeftInWave;
                }
                return;
            }
            while (mobsLeftInWave + mobsToKillInWave * 0.1F <= lastMobsLeftInWave) {
                boundPlayers.sendMessage(ChatFormatting.GREEN, "invmod.message.nexus.stabilizedto", "" + ChatFormatting.DARK_GREEN + (100 - 100 * mobsLeftInWave / mobsToKillInWave) + "%");
                lastMobsLeftInWave = ((int) (lastMobsLeftInWave - mobsToKillInWave * 0.1F));
            }
        } else if (reason == Entity.RemovalReason.DISCARDED) {
            if (combatant.asEntity().getType().create(getWorld()) instanceof Combatant<?> copy) {
                copy.asEntity().restoreFrom(combatant.asEntity());
                copy.setNexus(this);
                waveSpawner.askForRespawn(copy);
            }
        }
    }

    // TODO: Generate warning when a mob is nearby
    public void registerMobClose() {
    }

    @Override
    public boolean start(int startWave) {
        if (!storage.setActiveNexus(this)) {
            InvasionMod.LOGGER.warn("Another nexus is already active in this world");
        }
        if (mode == Mode.CONTINUOUS && continuousAttack) {
            boundPlayers.sendWarning("invmod.message.nexus.alreadyactivated");
            return false;
        }

        if (mode != Mode.STOPPED && mode != Mode.CONTINUOUS) {
            InvasionMod.LOGGER.warn("Tried to activate Nexus while already active");
            return false;
        }

        if (!waveSpawner.isReady()) {
            InvasionMod.LOGGER.warn("Wave spawner is not in ready state");
            return false;
        }

        try {
            boundingBoxToRadius = computeSpawnArea();
            currentWave = startWave;
            waveSpawner.beginNextWave(currentWave);
            setMode(mode == Mode.STOPPED ? Mode.STARTED : Mode.WAITING);
            boundPlayers.bindPlayers(boundingBoxToRadius);
            regenerateHealth();
            waveDelayTimer = -1L;
            System.out.println(boundPlayers.getParticipantsList().getContents().toString());
            boundPlayers.sendMessage(boundPlayers.getParticipantsList());
            boundPlayers.sendWarning("invmod.message.nexus.firstwavesoon");
            //boundPlayers.playSoundForBoundPlayers(InvSounds.BLOCK_NEXUS_RUMBLE);
            activated = true;
            return true;
        } catch (WaveSpawnerException e) {
            stop(false);
            InvasionMod.LOGGER.info(e.getMessage());
            boundPlayers.sendNotice(e.getMessage());
            return false;
        }
    }

    private void startContinuousPlay() {
        if (mode != Mode.STABLE || !waveSpawner.isReady()) {
            boundPlayers.sendWarning("invmod.message.nexus.couldnotactivate");
            return;
        }
        boundingBoxToRadius = getChunkBox(getWorld());
        setMode(Mode.CONTINUOUS);
        regenerateHealth();
        lastPowerLevel = powerLevel;
        lastWorldTime = getWorld().getGameTime();
        nextAttackTime = (int) ((lastWorldTime / TICKS_PER_DAY * TICKS_PER_DAY) + HALF_DAY_TIME);
        if (lastWorldTime % TICKS_PER_DAY > SUNSET_TIME && lastWorldTime % TICKS_PER_DAY < NIGHT_TIME) {
            boundPlayers.sendWarning("invmod.message.nexus.nightlooming");
        } else {
            boundPlayers.sendWarning("invmod.message.nexus.activatedandstable");
        }
    }

    private void doInvasion(int elapsed) throws WaveSpawnerException {
        if (waveSpawner.isActive()) {
            if (hp <= 0) {
                theEnd();
            } else {
                nexusItemStacks.generateFlux(1);
                if (waveSpawner.isWaveComplete()) {
                    if (waveDelayTimer == -1L) {
                        boundPlayers.sendMessage(ChatFormatting.GREEN, "invmod.message.wave.complete", "" + ChatFormatting.DARK_GREEN + currentWave);
                        //boundPlayers.playSoundForBoundPlayers(InvSounds.BLOCK_NEXUS_CHIME);
                        waveDelayTimer = 0L;
                        waveDelay = waveSpawner.getWaveRestTime();
                        InvasionMod.LOGGER.info("Next wave begins in: {}ticks", waveDelay);
                    } else {
                        waveDelayTimer += elapsed;
                        if (waveDelayTimer > waveDelay) {
                            currentWave += 1;
                            boundPlayers.sendWarning("invmod.message.wave.begin", "" + ChatFormatting.DARK_RED + currentWave);
                            waveSpawner.beginNextWave(currentWave);
                            waveDelayTimer = -1L;
                           // boundPlayers.playSoundForBoundPlayers(InvSounds.BLOCK_NEXUS_RUMBLE);
                            if (currentWave > nexusLevel) {
                                nexusLevel = currentWave;
                            }
                        }
                    }
                } else {
                    waveSpawner.spawn(elapsed);
                }
            }
        }
    }

    private void doContinuous(int elapsed) {
        powerLevelTimer += elapsed;
        if (powerLevelTimer > MAX_POWER_LEVEL) {
            powerLevelTimer -= MAX_POWER_LEVEL;
            nexusItemStacks.generateFlux(5 + (int) (5 * powerLevel / 1550F));
            if (!nexusItemStacks.getItem(0).is(ModItems.DAMPING_AGENT.get())) {
                powerLevel++;
            }
        }

        if (nexusItemStacks.getItem(0).is(ModItems.STRONG_DAMPING_AGENT.get()) && powerLevel >= 0 && !continuousAttack && --powerLevel < 0) {
            stop(false);
        }

        if (!continuousAttack) {
            long currentTime = getWorld().getGameTime();
            int timeOfDay = (int) (this.lastWorldTime % TICKS_PER_DAY);
            if (timeOfDay < SUNSET_TIME && currentTime % TICKS_PER_DAY >= SUNSET_TIME && currentTime + SUNSET_TIME > nextAttackTime) {
                boundPlayers.sendWarning("invmod.message.nexus.nightlooming");
            }
            if (lastWorldTime > currentTime) {
                nextAttackTime = ((int) (nextAttackTime - (lastWorldTime - currentTime)));
            }
            lastWorldTime = currentTime;

            if (lastWorldTime >= nextAttackTime) {
                try {
                    float difficulty = 1 + powerLevel / 4500;
                    float tierLevel = 1 + powerLevel / 4500;
                    Wave wave = waveBuilder.generateWave(difficulty, tierLevel, WAVE_DURATION);
                    mobsLeftInWave = (lastMobsLeftInWave = mobsToKillInWave = (int) (wave.getTotalMobAmount() * 0.8F));
                    waveSpawner.beginNextWave(wave);
                    continuousAttack = true;
                    int days = getWorld().getRandom().nextIntBetweenInclusive(config.minContinuousModeDays, config.maxContinuousModeDays);
                    nextAttackTime = (int) ((currentTime / TICKS_PER_DAY * TICKS_PER_DAY) + HALF_DAY_TIME + days * TICKS_PER_DAY);
                    regenerateHealth();
                    zapTimer = 0;
                    waveDelayTimer = -1L;
                    boundPlayers.sendWarning("invmod.message.nexus.destabilizing");
                    //boundPlayers.playSoundForBoundPlayers(InvSounds.BLOCK_NEXUS_RUMBLE);
                } catch (WaveSpawnerException e) {
                    InvasionMod.LOGGER.error("Exception whilst updating spawner", e);
                    stop(false);
                }
            }

        } else if (hp <= 0) {
            continuousAttack = false;
            continuousNexusHurt();
        } else if (waveSpawner.isWaveComplete()) {
            if (waveDelayTimer == -1L) {
                waveDelayTimer = 0L;
                waveDelay = waveSpawner.getWaveRestTime();
            } else {
                waveDelayTimer += elapsed;
                if (waveDelayTimer > waveDelay && zapTimer < -200) {
                    waveDelayTimer = -1L;
                    continuousAttack = false;
                    waveSpawner.stop();
                    regenerateHealth();
                    lastPowerLevel = powerLevel;
                }
            }

            zapTimer--;
            if (mobsLeftInWave <= 0) {
                if (zapTimer <= 0 && zapEnemy(true)) {
                    zapEnemy(false);
                    zapTimer = 23;
                }
            }
        } else {
            try {
                waveSpawner.spawn(elapsed);
            } catch (WaveSpawnerException e) {
                InvasionMod.LOGGER.error("Exception occured whilst spawning wave", e);
                stop(false);
            }
        }
    }

    private void regenerateHealth() {
        hp = MAX_HEALTH;
        lastHp = MAX_HEALTH;
    }

    public void tickInventory() {

        nexusItemStacks.tick(this);

        if (!storage.canActivate(this)) {
            return;
        }

        ItemStack catalyst = nexusItemStacks.getItem(0);

        if (activationTimer >= MAX_ACTIVAION_TIME) {
            activationTimer = 0;
            if (!catalyst.isEmpty()) {
                if (catalyst.is(ModItems.NEXUS_CATALYST.get())) {
                    catalyst.shrink(1);
                    start(1);
                } else if (catalyst.is(ModItems.STRONG_NEXUS_CATALYST.get())) {
                    catalyst.shrink(1);
                    start(10);
                } else if (catalyst.is(ModItems.STABLE_NEXUS_CATALYST.get())) {
                    catalyst.shrink(1);
                    activated = true;
                    startContinuousPlay();
                }
            }
        } else if (mode.isIdle()) {
            if (!catalyst.isEmpty()) {
                if (catalyst.is(ModItems.NEXUS_CATALYST.get()) || catalyst.is(ModItems.STRONG_NEXUS_CATALYST.get())) {
                    activationTimer++;
                    if (activationTimer % 100 == level.getRandom().nextInt(100)) {
                       // level.playSound(null, pos, InvSounds.BLOCK_NEXUS_RUMBLE, SoundCategory.BLOCKS, 1, 1);
                    }
                    setMode(Mode.STOPPED);
                } else if (catalyst.is(ModItems.STABLE_NEXUS_CATALYST.get())) {
                    activationTimer++;
                    if (activationTimer % 100 == level.getRandom().nextInt(100)) {
                     //   level.playSound(null, pos, InvSounds.BLOCK_NEXUS_RUMBLE, SoundCategory.BLOCKS, 1, 1);
                    }
                    setMode(Mode.STABLE);
                }
            } else {
                activationTimer = 0;
            }
        } else if (mode == Mode.CONTINUOUS) {
            if (!catalyst.isEmpty()) {
                if (catalyst.is(ModItems.NEXUS_CATALYST.get()) || catalyst.is(ModItems.STRONG_NEXUS_CATALYST.get())) {
                    activationTimer++;
                }
            } else {
                activationTimer = 0;
            }
        }
    }

    protected void setMode(Mode mode) {
        if (mode == this.mode) {
            return;
        }
        InvasionMod.LOGGER.info("Nexus {} changing mode from {} to {}", this.getUuid(), this.mode, mode);
        this.mode = mode;
        if (getWorld() instanceof ServerLevel sl) {
            if (sl.getBlockState(pos).is(ModBlocks.NEXUS.get())) {
                sl.setBlockAndUpdate(pos, ModBlocks.NEXUS.get().defaultBlockState().setValue(NexusBlock.ACTIVE, mode != Mode.STOPPED));
            } else {
                discard();
            }
        }
    }

    private int acquireEntities() {
        List<PathfinderMob> entities = getWorld().getEntitiesOfClass(PathfinderMob.class, boundingBoxToRadius.inflate(10, 128, 10), Combatant.PREDICATE);
        InvasionMod.LOGGER.info("Acquired " + entities.size() + " entities after state restore");
        return entities.size();
    }

    private void theEnd() {
        if (!getWorld().isClientSide()) {
            boundPlayers.sendWarning("invmod.message.nexus.destroyed");
            stop(false);
            boundPlayers.release();
            killAllMobs();
        }
    }

    private void continuousNexusHurt() {
        boundPlayers.sendWarning("invmod.message.nexus.severelydamaged");
        boundPlayers.playSoundForBoundPlayers(SoundEvents.ENDER_DRAGON_DEATH, 4, 1);
        killAllMobs();
        waveSpawner.stop();
        powerLevel = ((int) ((powerLevel - (powerLevel - lastPowerLevel)) * 0.7F));
        lastPowerLevel = powerLevel;
        if (powerLevel < 0) {
            powerLevel = 0;
            stop(false);
        }
    }

    private void killAllMobs() {
        DamageSource source = getWorld().damageSources().magic();
        for (LivingEntity mob : getWorld().getEntitiesOfClass(LivingEntity.class, boundingBoxToRadius, Combatant.PREDICATE)) {
            mob.hurt(source, mob.getMaxHealth());
            mob.kill();
        }
    }

    private boolean zapEnemy(boolean sfx) {
        Combatant<?> mob = mobList.removeNearestCombatant();
        if (mob == null) {
            return false;
        }
        mob.asEntity().hurt(mob.asEntity().damageSources().magic(), 500);
       // getWorld().addFreshEntity(new ElectricityBoltEntity(getWorld(), pos.getCenter(), mob.asEntity().getEyePosition(), 15, sfx)); //Add this entity
        return true;
    }

    private boolean resumeSpawnerContinuous() {
        try {
            float difficulty = 1 + powerLevel / 4500F;
            float tierLevel = 1 + powerLevel / 4500F;
            Wave wave = waveBuilder.generateWave(difficulty, tierLevel, WAVE_DURATION);
            this.mobsToKillInWave = ((int) (wave.getTotalMobAmount() * 0.8F));
            InvasionMod.LOGGER.info("Original mobs to kill: " + mobsToKillInWave);
            lastMobsLeftInWave = mobsToKillInWave - waveSpawner.resumeFromState(wave);
            mobsLeftInWave = lastMobsLeftInWave;
            return true;
        } catch (WaveSpawnerException e) {
            InvasionMod.LOGGER.error("Error resuming spawner", e);
            stop(false);
            return false;
        }
    }

    private boolean resumeSpawnerInvasion() {
        try {
            waveSpawner.resumeFromState(currentWave);
            return true;
        } catch (WaveSpawnerException e) {
            InvasionMod.LOGGER.error("Error resuming spawner", e);
            stop(false);
            return false;
        }
    }

    Nexus(ServerLevel level, WorldNexusStorage storage, CompoundTag tag, HolderLookup.Provider registries) {
        this(level, storage, tag.getUUID("uuid"), NbtUtils.readBlockPos(tag, "pos").orElseThrow());
        activationTimer = tag.getInt("activationTimer");
        mode = Mode.forId(tag.getInt("mode"));
        currentWave = tag.getInt("currentWave");
        nexusLevel = tag.getInt("nexusLevel");
        hp = tag.getInt("hp");
        nexusKills = tag.getInt("nexusKills");
        powerLevel = tag.getInt("powerLevel");
        lastPowerLevel = tag.getInt("lastPowerLevel");
        nextAttackTime = tag.getInt("nextAttackTime");
        daysToAttack = tag.getInt("daysToAttack");
        continuousAttack = tag.getBoolean("continuousAttack");
        activated = tag.getBoolean("activated");

        nexusItemStacks.loadAdditional(tag.getCompound("inventory"), registries);
        boundPlayers.loadAdditional(tag.getCompound("boundPlayers"), registries);
        waveSpawner.loadAdditional(tag.getCompound("waveSpawner"), registries);
        attackerAI.loadAdditional(tag.getCompound("ai"), registries);

        boundingBoxToRadius = computeSpawnArea();
    }

    public CompoundTag saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        tag.putUUID("uuid", uuid);
        tag.put("pos", NbtUtils.writeBlockPos(pos));
        tag.putInt("activationTimer", activationTimer);
        tag.putInt("mode", getMode().ordinal());
        tag.putInt("currentWave", getCurrentWave());
        tag.putInt("nexusLevel", getLevel());
        tag.putInt("hp", hp);
        tag.putInt("nexusKills", nexusKills);
        tag.putInt("powerLevel", powerLevel);
        tag.putInt("lastPowerLevel", lastPowerLevel);
        tag.putInt("nextAttackTime", nextAttackTime);
        tag.putInt("daysToAttack", daysToAttack);
        tag.putBoolean("continuousAttack", continuousAttack);
        tag.putBoolean("activated", isActive());

        tag.put("inventory", nexusItemStacks.saveAdditional(new CompoundTag(), registries));
        tag.put("boundPlayers", boundPlayers.saveAdditional(new CompoundTag(), registries));
        tag.put("waveSpawner", waveSpawner.saveAdditional(new CompoundTag(), registries));
        tag.put("ai", attackerAI.saveAdditional(new CompoundTag(), registries));
        return tag;
    }
}