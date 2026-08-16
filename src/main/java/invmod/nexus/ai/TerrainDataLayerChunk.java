package invmod.nexus.ai;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;

class TerrainDataLayerChunk implements ScaffoldView, BlockGetter {
    protected final Long2ObjectMap<Integer> data = new Long2ObjectOpenHashMap<>();
    private final BlockGetter world;

    public TerrainDataLayerChunk(BlockGetter world) {
        this.world = world;
    }

    public TerrainDataLayerChunk(BlockGetter world, Long2ObjectMap<Integer> dataLayer) {
        this.world = world;
        this.data.putAll(dataLayer);
    }

    @Override
    public void setData(BlockPos pos, int data) {
        this.data.put(pos.asLong(), Integer.valueOf(data));
    }

    @Override
    public int getData(BlockPos pos) {
        return data.getOrDefault(pos.asLong(), Integer.valueOf(0));
    }

    @Override
    public BlockEntity getBlockEntity(BlockPos pos) {
        return world.getBlockEntity(pos);
    }

    @Override
    public BlockState getBlockState(BlockPos pos) {
        return world.getBlockState(pos);
    }

    @Override
    public FluidState getFluidState(BlockPos pos) {
        return world.getFluidState(pos);
    }

    @Override
    public int getHeight() {
        return world.getHeight();
    }

    @Override
    public int getBottomY() {
        return world.getMinBuildHeight();
    }
}