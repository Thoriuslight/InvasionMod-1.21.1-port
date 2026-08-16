package invmod.nexus;

import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;

import invmod.block.ModBlocks;
import invmod.block.entity.NexusBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;


import com.mojang.datafixers.util.Pair;

import net.minecraft.nbt.NbtOps;

public interface IHasNexus {

    Handle getNexusHandle();

    @Nullable
    default NexusAccess getNexus() {
        return getNexusHandle().get();
    }

    default void setNexus(@Nullable NexusAccess nexus) {
        getNexusHandle().set(nexus);
    }

    default boolean hasNexus() {
        return getNexus() != null;
    }

    double findDistanceToNexus();

    @Nullable
    static NexusAccess findNexus(Level level, BlockPos center) {
        for (BlockPos pos : BlockPos.withinManhattan(center, 8, 5, 8)) {
            if (level.getBlockState(pos).is(ModBlocks.NEXUS.get())) {
                if (level.getBlockEntity(pos) instanceof NexusBlockEntity nexus) {
                    return nexus.getNexus();
                }
            }
        }
        return null;
    }

    public final class Handle {
        @Nullable
        private UUID nexusId;
        @Nullable
        private GlobalPos globalPos;
        @Nullable
        private NexusAccess nexus;

        private final Supplier<Level> worldGetter;

        public Handle(Supplier<Level> worldGetter) {
            this.worldGetter = worldGetter;
        }

        public @Nullable NexusAccess get() {
            if (nexusId != null
                    && globalPos != null
                    && nexus == null
                    && worldGetter.get() instanceof ServerLevel sl
                    && sl.getServer().getLevel(globalPos.dimension()) instanceof ServerLevel level) {
                nexus = WorldNexusStorage.of(level).getNexus(nexusId);
                if (nexus == null) {
                    set(null);
                }
            }
            return nexus;
        }

        public void set(@Nullable NexusAccess nexus) {
            nexusId = nexus == null ? null : nexus.getUuid();
            globalPos = nexus == null ? null : GlobalPos.of(nexus.getWorld().dimension(), nexus.getOrigin());
            this.nexus = nexus;
        }

        public void loadAdditional(CompoundTag tag) {
            nexus = null;
            globalPos = tag.contains("globalPos") ? GlobalPos.CODEC.decode(NbtOps.INSTANCE, tag.get("globalPos")).result().map(Pair::getFirst).orElse(null) : null;
            nexusId = tag.hasUUID("nexusId") ? tag.getUUID("nexusId") : null;
        }

        public Optional<GlobalPos> getPos() {
            return Optional.ofNullable(globalPos);
        }

        public CompoundTag saveAdditional(CompoundTag tag) {
            if (nexusId != null) {
                tag.putUUID("nexusId", nexusId);
            }
            if (globalPos != null) {
                GlobalPos.CODEC.encodeStart(NbtOps.INSTANCE, globalPos).result().ifPresent(pos -> {
                    tag.put("globalPos", pos);
                });
            }
            return tag;
        }
    }
}