package invmod.item;

import invmod.block.BlockMetadata;
import invmod.block.NexusBlock;
import invmod.block.entity.NexusBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

/** Right-click any block; if a Nexus Block-Entity is nearby (8-block radius)
 *  prints its full state machine status. */
public class ProbeItem extends Item {
    private final boolean isProbe;

    public ProbeItem(Properties props, boolean isProbe) {
        super(props);
        this.isProbe = isProbe;
    }

    @Override
    public  InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        if (level.isClientSide()) return InteractionResult.PASS;

        BlockPos pos = context.getClickedPos();
        BlockState state = level.getBlockState(pos);
        Player player =  context.getPlayer();
        if (player == null) {
            return InteractionResult.FAIL;
        }
        // set nexus range
        if(state.getBlock() instanceof NexusBlock) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof NexusBlockEntity nexus) {
                //int newRange = nexus.getSpawnRadius();

                // check if the player wants to increase or decrease the range
                //newRange += player.isCrouching() ? -8 : 8;
                // TODO: this check should be handled by the block entity, not here
               // newRange = Mth.clamp(newRange, 32, 128);

                //if (nexus.setSpawnRadius(newRange)) {
                //    player.sendMessage(Text.translatable("invmod.message.probe.rangechanged", Text.literal(nexus.getSpawnRadius() + "").formatted(Formatting.GREEN)).formatted(Formatting.DARK_GREEN));
                //} else if (nexus.isActive()) {
                //    player.sendMessage(Text.translatable("invmod.message.probe.cannotchangerange", Text.literal(nexus.getSpawnRadius() + "")).formatted(Formatting.RED));
               // }
                return InteractionResult.SUCCESS;
            }
        }
        // display block strength
        if (isProbe) {
            float blockStrength = BlockMetadata.getStrength(pos, state, level);
            int strengthRounded = (int) ((blockStrength + 0.005D) * 100.0D) / 10;
            player.displayClientMessage(Component.translatable("message.invmod.probe.blockstrength",
                    Component.literal(strengthRounded + "" ).withColor(0x00FF00)).withColor(0x008000),true);
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.FAIL;
    }
}
