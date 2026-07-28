package invmod.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class BlockMetadata {
    public static float getStrength(BlockPos pos, BlockState state, Level level) {
        int bonus = 0;
        //BlockPos.Mutable mutable = pos.mutableCopy();
        float strength = state.getDestroySpeed(level, pos);
        //switch (BlockSpecial.of(state)) {
        //    case CONSTRUCTION_BRICKS:
         //       for (Direction direction : Direction.values()) {
         //           if (world.getBlockState(mutable.set(pos).move(direction)).isOf(state.getBlock())) {
          //              bonus++;
         //           }
           //     }
          /*      break;
            case CONSTRUCTION_STONE:
                for (Direction direction : Direction.values()) {
                    if (world.getBlockState(mutable.set(pos).move(direction)).isIn(InvTags.Blocks.STONE_CONSTRUCTION_BONUS_MATERIALS)) {
                        bonus++;
                    }
                }
                break;
            default:
        }*/
        return strength * (1 + bonus * 0.1F);
    }
}
