package invmod.block;

import com.mojang.serialization.MapCodec;
import invmod.block.entity.ModBlockEntities;
import invmod.item.ModItems;
import invmod.block.entity.NexusBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class NexusBlock extends BaseEntityBlock {
    public static final MapCodec<NexusBlock> CODEC = simpleCodec(NexusBlock::new);
    public static final BooleanProperty ACTIVE = BlockStateProperties.LIT;


    public NexusBlock(Properties props) {
        super(props);
        this.registerDefaultState(this.stateDefinition.any().setValue(ACTIVE, false));
    }
    @Override
    protected @NotNull RenderShape getRenderShape(@NotNull BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    protected @NotNull MapCodec<NexusBlock> codec() {
        return CODEC;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<net.minecraft.world.level.block.Block, BlockState> builder) {
        builder.add(ACTIVE);
    }

    @Override
    public @NotNull BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return new NexusBlockEntity(pos, state);
    }



    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        if (!state.getValue(ACTIVE)) return;
        for (int i = 0; i < 4; i++) {
            double x = pos.getX() + 0.5 + (random.nextDouble() - 0.5) * 0.6;
            double y = pos.getY() + 1.05 + random.nextDouble() * 0.3;
            double z = pos.getZ() + 0.5 + (random.nextDouble() - 0.5) * 0.6;
            level.addParticle(ParticleTypes.SOUL_FIRE_FLAME, x, y, z, 0, 0.02, 0);
            level.addParticle(ParticleTypes.PORTAL,
                    pos.getX() + random.nextDouble(),
                    pos.getY() + random.nextDouble() * 1.5,
                    pos.getZ() + random.nextDouble(),
                    (random.nextDouble() - 0.5) * 0.5, random.nextDouble() * 0.5, (random.nextDouble() - 0.5) * 0.5);
        }
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos,
                                              Player player, InteractionHand hand, BlockHitResult hit) {
        if (!level.isClientSide() && !stack.is(ModItems.MATERIAL_PROBE.get()) && !stack.is(ModItems.NEXUS_ADJUSTER.get()) && !stack.is(ModItems.DEBUG_WAND.get())) {
            if(level.getBlockEntity(pos) instanceof NexusBlockEntity nexus)
            ((ServerPlayer) player).openMenu(new SimpleMenuProvider(nexus, Component.literal("Nexus")), pos);
            return ItemInteractionResult.SUCCESS;
        }

        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean moved) {
        if (state.getBlock() != newState.getBlock()) {
            if(level.getBlockEntity(pos) instanceof NexusBlockEntity nexus){
                nexus.discard();
            }
        }
        super.onRemove(state, level, pos, newState, moved);
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        if (level.isClientSide()) return null;
        return createTickerHelper(type, ModBlockEntities.NEXUS.get(),
                (lvl, pos, st, nexus) -> nexus.tick((ServerLevel) lvl, pos, st));
    }

    @Override
    protected float getDestroyProgress(BlockState state, Player player, BlockGetter level, BlockPos pos) {
        return state.getValue(ACTIVE) ? -1 : super.getDestroyProgress(state, player, level, pos);
    }
}
