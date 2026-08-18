package invmod.util.math;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.util.Mth;

import java.util.List;

public interface PosUtils {
    @Deprecated
    Direction[] CARDINAL_DIRECTIONS = { Direction.EAST, Direction.WEST, Direction.SOUTH, Direction.NORTH };

    List<BlockPos> ZERO = List.of();
    List<Vec3i> OFFSET_ADJACENT = List.of(
            new BlockPos( 1, 0, 0),
            new BlockPos(-1, 0, 0),
            new BlockPos( 0, 0, 1),
            new BlockPos( 0, 0, -1)
    );
    List<Vec3i> OFFSET_ADJACENT_2 = List.of(
            new BlockPos( 2, 0, 0),
            new BlockPos( 2, 0, 1),
            new BlockPos(-1, 0, 1),
            new BlockPos(-1, 0, 0),
            new BlockPos( 1, 0, 2),
            new BlockPos( 0, 0, 2),
            new BlockPos( 0, 0, -1),
            new BlockPos( 1, 0, -1)
    );
    List<Vec3i> OFFSET_RING = List.of(
            new BlockPos( 1, 0, 1),
            new BlockPos( 0, 0, 1),
            new BlockPos(-1, 0, 1),
            new BlockPos(-1, 0, 0),
            new BlockPos(-1, 0,-1),
            new BlockPos( 0, 0,-1),
            new BlockPos( 1, 0, 0)
    );

    static double getInclination(BlockPos from, BlockPos to) {
        BlockPos delta = from.subtract(to);
        if (delta.getY() <= 0) {
            return 0;
        }
        return (delta.getY() + 8) / (Math.sqrt(Mth.square(delta.getX()) + Mth.square(delta.getZ())) + Mth.EPSILON);
    }
}