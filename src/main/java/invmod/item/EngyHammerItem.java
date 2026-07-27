package invmod.item;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LadderBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

/** Engineer's Hammer: right-click a block face to place a Ladder there,
 *  attached to that face. Consumes 1 durability per use. */
public class EngyHammerItem extends Item {
    public EngyHammerItem(Properties props) { super(props); }

    @Override
    public InteractionResult useOn(UseOnContext ctx) {
        Level level = ctx.getLevel();
        BlockPos clicked = ctx.getClickedPos();
        Direction face = ctx.getClickedFace();
        if (face.getAxis() == Direction.Axis.Y) return InteractionResult.PASS;
        BlockPos placeAt = clicked.relative(face);
        if (!level.getBlockState(placeAt).canBeReplaced()) return InteractionResult.PASS;

        if (level.isClientSide()) return InteractionResult.SUCCESS;

        BlockState ladder = Blocks.LADDER.defaultBlockState()
                .setValue(LadderBlock.FACING, face);
        level.setBlock(placeAt, ladder, 3);
        level.playSound(null, placeAt, SoundEvents.LADDER_PLACE, SoundSource.BLOCKS, 1.0F, 1.0F);
        if (ctx.getPlayer() != null && !ctx.getPlayer().getAbilities().instabuild) {
            ItemStack stack = ctx.getItemInHand();
            stack.hurtAndBreak(1, ctx.getPlayer(), net.minecraft.world.entity.LivingEntity.getSlotForHand(ctx.getHand()));
        }
        return InteractionResult.CONSUME;
    }
}
