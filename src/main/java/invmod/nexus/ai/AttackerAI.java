package invmod.nexus.ai;


import invmod.block.entity.NexusBlockEntity;
import invmod.nexus.Combatant;
import invmod.nexus.Nexus;
import invmod.nexus.ai.scaffold.ScaffoldList;
import invmod.nexus.ai.scaffold.ScaffoldView;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.level.CollisionGetter;

import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;



public class AttackerAI {
    private static final ExecutorService SCAFFOLD_EXECUTOR = Executors.newSingleThreadExecutor();
    private final Nexus nexus;

    private final Long2ObjectMap<Integer> entityDensityData = new Long2ObjectOpenHashMap<>();

    private final ScaffoldList scaffolds = new ScaffoldList();

    private int nextScaffoldCalcTimer;
    private int updateScaffoldTimer;
    private int nextEntityDensityUpdate;

    public AttackerAI(Nexus nexus) {
        this.nexus = nexus;

    }

    public ScaffoldList getScaffolds() {
        return scaffolds;
    }

    public void tick() {
        nextScaffoldCalcTimer = Math.max(0, nextScaffoldCalcTimer - 1);
        if (--updateScaffoldTimer <= 0) {
            updateScaffoldTimer = 40;
            scaffolds.tick(nexus.getWorld());
        }

        if (--nextEntityDensityUpdate <= 0) {
            nextEntityDensityUpdate = 20;
            entityDensityData.clear();
            for (Combatant<?> mob : nexus.getCombatants()) {
                entityDensityData.compute(mob.asEntity().blockPosition().asLong(), (key, old) -> (old == null ? 1 : old + 1) & ScaffoldView.MOB_DENSITY_FLAG);
            }
        }
    }

    public CollisionGetter wrapEntityData(CollisionGetter terrainMap) {
        return new TerrainDataLayer(terrainMap, entityDensityData);
    }

    public CollisionView addScaffoldDataTo(CollisionView view) {
        ScaffoldView terrainMap = ScaffoldView.of(view);
        BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();
        for (Scaffold scaffold : scaffolds) {
            BlockPos pos = scaffold.getNode().pos();
            for (int y = scaffold.getNode().bottom(); y < scaffold.getNode().top(); y++) {
                terrainMap.addScaffoldPosition(mutable.set(pos.getX(), y, pos.getZ()));
            }
        }
        return view;
    }

    public void requestBuildJob(NexusBlockEntity entity, Consumer<Optional<BlockPos>> callback) {
        if (nextScaffoldCalcTimer > 0 || scaffolds.size() > getScaffoldLimit()) {
            callback.accept(Optional.empty());
        } else {
            nextScaffoldCalcTimer = 200;
            boolean success = scaffolds.addAll(nexus, new ScaffoldGenerator(this).generateScaffolds(entity));
            if (success) {
                callback.accept(scaffolds.getNearest(entity.getBlockPos()));
            } else {
                callback.accept(Optional.empty());
            }
        }
    }

    private int getScaffoldLimit() {
        return 2 + nexus.getCurrentWave() / 2;
    }

    public int getScaffoldSpacing() {
        return 90 / (nexus.getCurrentWave() + 10);
    }

    public void loadAdditional(CompoundTag tag, HolderLookup.Provider registries)  {
        scaffolds.load(tag,.getList("scaffolds", ListTag.TAG_COMPOUND)
                .stream()
                .map(element -> new Scaffold((NbtCompound) element, nexus))
                .toList()
        );
    }

    public CompoundTag saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        ListTag nbttaglist = new ListTag();
        for (Scaffold scaffold : scaffolds) {
            nbttaglist.add(scaffold.toNBT(new NbtCompound()));
        }
        tag.put("scaffolds", nbttaglist);
        return tag;
    }
}