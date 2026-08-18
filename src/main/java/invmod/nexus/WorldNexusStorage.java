package invmod.nexus;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import com.mojang.datafixers.types.templates.CompoundList;
import invmod.InvasionMod;
import invmod.block.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.TagTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.saveddata.SavedData;
import org.jetbrains.annotations.Nullable;



public class WorldNexusStorage extends SavedData {
    private static final ResourceLocation ID = InvasionMod.id("nexus");

    public static Factory<WorldNexusStorage> getType(ServerLevel level) {
        return new SavedData.Factory<>(() -> new WorldNexusStorage(level), (nbt, lookup) -> new WorldNexusStorage(level, nbt, lookup), DataFixTypes.LEVEL);
    }

    public static WorldNexusStorage of(ServerLevel level) {
        return level.getDataStorage().computeIfAbsent(getType(level), ID.toLanguageKey());
    }

    private final ServerLevel level;

    private final Map<UUID, Nexus> instances = new HashMap<>();

    private Optional<UUID> activeNexus = Optional.empty();

    private boolean resumed;
    private int cleanupTimer;

    private WorldNexusStorage(ServerLevel level) {
        this.level = level;
    }

    private WorldNexusStorage(ServerLevel world, CompoundTag tag, HolderLookup.Provider registries) {
        this(world);
        resumed = true;
        if (tag.hasUUID("activeNexus")) {
            activeNexus = Optional.of(tag.getUUID("activeNexus"));
        }
        tag.getList("nexuses", ListTag.TAG_COMPOUND).forEach(i -> {
            Nexus nexus = new Nexus(world, this, (CompoundTag)i, registries);
            instances.put(nexus.getUuid(), nexus);
        });
    }

    public synchronized void tick() {
       cleanupTimer = (cleanupTimer + 1) % 40;
        instances.values().removeIf(nexus -> {
            if (tickCleanup(nexus)) {
                return true;
            }
            nexus.tickInventory();
            nexus.getAttackerAI().tick();
            if (resumed) {
                nexus.onLoaded();
            }
            nexus.tick();
            return nexus.isDiscarded();
        });
        resumed = false;

        activeNexus = activeNexus.filter(nexusId -> {
            Nexus nexus = instances.get(nexusId);
            return nexus != null && (nexus.isActivating() || nexus.isActive());
        });

        if (!instances.isEmpty()) {
            setDirty();
        }
    }

    private boolean tickCleanup(Nexus nexus) {
        if (cleanupTimer == 0 && !level.getBlockState(nexus.getOrigin()).is(ModBlocks.NEXUS.get())) {
            nexus.stop(true);
            InvasionMod.LOGGER.warn("Stranded Nexus entity trying to delete itself...");
            return true;
        }
        return false;
    }


    public synchronized Nexus getOrCreate(UUID nexusId, BlockPos pos) {
        return instances.computeIfAbsent(nexusId, id -> new Nexus(level, this, nexusId, pos));
    }

    public synchronized void destroyNexus(UUID nexusId) {
        @Nullable
        Nexus nexus = instances.remove(nexusId);
        if (nexus != null) {
            nexus.stop(true);
        }
    }

    public synchronized NexusAccess getNexus(UUID nexusId) {
        return instances.get(nexusId);
    }

    public synchronized Optional<? extends ControllableNexusAccess> getNexus() {
        return activeNexus.map(instances::get);
    }

    public synchronized boolean canActivate(Nexus nexus) {
        return activeNexus.map(instances::get).orElse(nexus) == nexus;
    }

    public synchronized boolean setActiveNexus(Nexus nexus) {
        if (!canActivate(nexus)) {
            return false;
        }
        activeNexus = Optional.ofNullable(nexus).map(Nexus::getUuid);
        setDirty();
        return true;
    }

    @Override
    public CompoundTag save(CompoundTag tag, HolderLookup.Provider registries) {
        activeNexus.ifPresent(nexus -> {
            tag.putUUID("activeNexus", nexus);
        });
        ListTag nexuses = new ListTag();
        instances.forEach((uuid, nexus) -> {
            nexuses.add(nexus.saveAdditional(new CompoundTag(), registries));
        });
        tag.put("nexuses", nexuses);
        return tag;
    }
}