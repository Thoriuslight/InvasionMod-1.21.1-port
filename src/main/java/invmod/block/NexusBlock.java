package invmod.block;

import com.mojang.serialization.MapCodec;
import invmod.ModBlockEntities;
import invmod.ModItems;
import invmod.block.entity.NexusBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
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
import org.jetbrains.annotations.Nullable;

public final class NexusBlock extends BaseEntityBlock {
    public static final BooleanProperty ACTIVE = BlockStateProperties.LIT;
    public static final MapCodec<NexusBlock> CODEC = simpleCodec(NexusBlock::new);

    public NexusBlock(Properties props) {
        super(props);
        this.registerDefaultState(this.stateDefinition.any().setValue(ACTIVE, false));
    }

    @Override
    protected MapCodec<NexusBlock> codec() { return CODEC; }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<net.minecraft.world.level.block.Block, BlockState> builder) {
        builder.add(ACTIVE);
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new NexusBlockEntity(pos, state);
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        if (level.isClientSide()) return null;
        return createTickerHelper(type, ModBlockEntities.NEXUS.get(),
                (lvl, pos, st, be) -> be.serverTick((ServerLevel) lvl));
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) { return RenderShape.MODEL; }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        if (!Boolean.TRUE.equals(state.getValue(ACTIVE))) return;
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

    /**
     * Right-click interaction:
     *   bare hand                  -> show status
     *   nexus_catalyst             -> bind player + start wave 1 (consume 1)
     *   damping_agent              -> stop the invasion (consume 1)
     *   nexus_adjuster             -> cycle the spawn radius
     */
    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos,
                                              Player player, InteractionHand hand, BlockHitResult hit) {
        if (!(level instanceof ServerLevel server)) {
            return ItemInteractionResult.sidedSuccess(level.isClientSide());
        }
        if (!(level.getBlockEntity(pos) instanceof NexusBlockEntity nexus)) {
            return ItemInteractionResult.FAIL;
        }

        Item held = stack.getItem();
        if (held == ModItems.NEXUS_CATALYST.get() || held == ModItems.STABLE_NEXUS_CATALYST.get()) {
            nexus.bindPlayer(player);
            nexus.startWave(player, server);
            if (!player.getAbilities().instabuild) stack.shrink(1);
            return ItemInteractionResult.sidedSuccess(false);
        }
        if (held == ModItems.DAMPING_AGENT.get() || held == ModItems.STRONG_DAMPING_AGENT.get()) {
            nexus.stopWave(server);
            if (!player.getAbilities().instabuild) stack.shrink(1);
            return ItemInteractionResult.sidedSuccess(false);
        }
        if (held == ModItems.NEXUS_ADJUSTER.get()) {
            nexus.cycleSpawnRadius();
            player.displayClientMessage(Component.literal("Nexus radius -> " + nexus.getSpawnRadius()), true);
            return ItemInteractionResult.sidedSuccess(false);
        }

        // Default: open Nexus control GUI on the server side.
        if (player instanceof net.minecraft.server.level.ServerPlayer sp) {
            sp.openMenu(new net.minecraft.world.SimpleMenuProvider(
                    (id, inv, p) -> new invmod.menu.NexusMenu(id, inv, pos, nexus.asContainerData()),
                    Component.literal("Nexus")
            ), buf -> buf.writeBlockPos(pos));
        }
        return ItemInteractionResult.sidedSuccess(false);
    }

    private static String modeName(int mode) {
        return switch (mode) {
            case NexusBlockEntity.MODE_IDLE        -> "idle";
            case NexusBlockEntity.MODE_SPAWNING    -> "spawning";
            case NexusBlockEntity.MODE_AWAIT_CLEAR -> "await-clear";
            case NexusBlockEntity.MODE_COOLDOWN    -> "cooldown";
            default -> "?";
        };
    }
}
