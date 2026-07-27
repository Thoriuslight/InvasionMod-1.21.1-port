package invmod.entity.ai;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.event.EventHooks;

import java.util.EnumSet;

/**
 * Custom AI goal: when a mob has a target it can't reach via vanilla
 * pathing, break the block immediately in front of it (head or body height
 * or above for vertical tunneling) every {@code BREAK_INTERVAL_TICKS}.
 * Per-mob max hardness controls which blocks can be tunneled.
 *
 * Respects {@link GameRules#RULE_MOBGRIEFING} via {@link EventHooks#canEntityGrief}.
 */
public class BreakBlocksTowardTargetGoal extends Goal {
    private static final int BREAK_INTERVAL_TICKS = 40;
    private static final double STUCK_DISTANCE_SQ = 2.5 * 2.5;

    private final Mob mob;
    private final float maxHardness;
    private int ticker;

    public BreakBlocksTowardTargetGoal(Mob mob) { this(mob, 6.0F); }

    public BreakBlocksTowardTargetGoal(Mob mob, float maxHardness) {
        this.mob = mob;
        this.maxHardness = maxHardness;
        this.setFlags(EnumSet.noneOf(Flag.class));
    }

    @Override
    public boolean canUse() {
        LivingEntity target = mob.getTarget();
        if (target == null) return false;
        if (!(mob.level() instanceof ServerLevel server)) return false;
        if (!EventHooks.canEntityGrief(server, mob)) return false;
        // Only when navigation is failing or done but the target is still distant.
        return mob.getNavigation().isDone() && mob.distanceToSqr(target) > STUCK_DISTANCE_SQ;
    }

    @Override
    public boolean canContinueToUse() { return canUse(); }

    @Override
    public void tick() {
        LivingEntity target = mob.getTarget();
        if (target != null) {
            // Push mob physically toward the target so it contacts the wall
            // it needs to dig through (vanilla navigation has given up).
            mob.getLookControl().setLookAt(target, 30.0F, 30.0F);
            double dx = target.getX() - mob.getX();
            double dz = target.getZ() - mob.getZ();
            double len = Math.sqrt(dx * dx + dz * dz);
            if (len > 0.001) {
                mob.getMoveControl().setWantedPosition(
                        mob.getX() + dx / len * 1.5,
                        mob.getY(),
                        mob.getZ() + dz / len * 1.5,
                        1.0);
            }
        }

        ticker++;
        if (ticker < BREAK_INTERVAL_TICKS) return;
        ticker = 0;
        tryBreakBlockInFront();
    }

    private void tryBreakBlockInFront() {
        Level level = mob.level();
        BlockPos at = mob.blockPosition();
        Direction facing = mob.getDirection();
        BlockPos[] candidates = new BlockPos[] {
                at.relative(facing).above(),  // head height
                at.relative(facing),          // body height
                at.above(),                   // straight up (for vertical tunnels)
        };
        for (BlockPos pos : candidates) {
            BlockState s = level.getBlockState(pos);
            if (s.isAir()) continue;
            float h = s.getDestroySpeed(level, pos);
            if (h < 0 || h > maxHardness) continue;
            level.destroyBlock(pos, true, mob);
            mob.swing(net.minecraft.world.InteractionHand.MAIN_HAND);
            return;
        }
    }
}
