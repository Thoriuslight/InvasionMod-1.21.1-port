package invmod.nexus.ai;


import java.util.List;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.CollisionGetter;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.swing.text.html.BlockView;

class TerrainDataLayer extends TerrainDataLayerChunk implements CollisionGetter {
    private final Long2ObjectMap<BlockGetter> chunks = new Long2ObjectOpenHashMap<>();
    private final CollisionGetter world;

    public TerrainDataLayer(CollisionGetter world) {
        super(world);
        this.world = world;
    }

    public TerrainDataLayer(CollisionGetter world, Long2ObjectMap<Integer> dataLayer) {
        this(world);
        this.data.putAll(dataLayer);
    }

    @Override
    public WorldBorder getWorldBorder() {
        return world.getWorldBorder();
    }

    @Override
    public BlockGetter getChunkForCollisions(int chunkX, int chunkZ) {
        BlockGetter chunk = world.getChunkForCollisions(chunkX, chunkZ);
        return chunk == null ? null : chunks.computeIfAbsent(ChunkPos.asLong(chunkX, chunkZ), l -> new TerrainDataLayerChunk(chunk));
    }

    @Override
    public List<VoxelShape> getEntityCollisions(Entity entity, AABB box) {
        return world.getEntityCollisions(entity, box);
    }
}