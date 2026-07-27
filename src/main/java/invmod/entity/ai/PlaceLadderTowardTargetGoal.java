package invmod.entity.ai;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LadderBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.neoforged.neoforge.event.EventHooks;

import java.util.EnumSet;

/**
 * PigEngy / engineer builder goal: when target is above mob and out of
 * direct reach, place a ladder block in front of the mob (attached to the
 * facing wall) up to {@code MAX_TOWER_HEIGHT} blocks high. Mirrors the
 * 1.7.2 {@code EntityIMPigEngy} ladder-tower behaviour.
 */
public class PlaceLadderTowardTargetGoal extends Goal {
    private static final int PLACE_INTERVAL_TICKS = 30;
    private static final int MAX_TOWER_HEIGHT     = 4;
    private static final double VERTICAL_TRIGGER  = 1.5;

    private final Mob mob;
    private int ticker;
    private int builtThisSession;

    public PlaceLadderTowardTargetGoal(Mob mob) {
        this.mob = mob;
        this.setFlags(EnumSet.noneOf(Flag.class));
    }

    @Override
    public boolean canUse() {
        LivingEntity target = mob.getTarget();
        if (target == null) return false;
        if (!(mob.level() instanceof ServerLevel server)) return false;
        if (!EventHooks.canEntityGrief(server, mob)) return false;
        // Target must be higher than the mob's eye height and not vastly far.
        double dy = target.getY() - mob.getY();
        return dy > VERTICAL_TRIGGER && mob.distanceToSqr(target) < 16.0 * 16.0;
    }

    @Override
    public boolean canContinueToUse() {
        return canUse() && builtThisSession < MAX_TOWER_HEIGHT;
    }

    @Override public void start() { this.builtThisSession = 0; this.ticker = 0; }

    @Override
    public void tick() {
        ticker++;
        if (ticker < PLACE_INTERVAL_TICKS) return;
        ticker = 0;
        if (tryPlaceLadder()) builtThisSession++;
    }

    private boolean tryPlaceLadder() {
        Level level = mob.level();
        Direction facing = mob.getDirection();
        BlockPos wallPos = mob.blockPosition().relative(facing);

        // Walk up the wall column until we hit an empty space.
        for (int dy = 0; dy < MAX_TOWER_HEIGHT; dy++) {
            BlockPos at = wallPos.above(dy);
            BlockState here = level.getBlockState(at);
            if (!here.isAir()) continue;

            // The block on the facing side must be solid for the ladder to attach.
            BlockPos back = at.relative(facing);
            BlockState backState = level.getBlockState(back);
            if (!backState.isFaceSturdy(level, back, facing.getOpposite())) {
                // Place a support block (cobblestone) behind the ladder.
                level.setBlock(back, Blocks.COBBLESTONE.defaultBlockState(), 3);
            }

            BlockState ladder = Blocks.LADDER.defaultBlockState()
                    .setValue(LadderBlock.FACING, facing.getOpposite());
            level.setBlock(at, ladder, 3);
            mob.swing(net.minecraft.world.InteractionHand.MAIN_HAND);
            return true;
        }
        return false;
    }
}
