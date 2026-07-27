package invmod.entity.ai;

import invmod.block.entity.NexusBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.EnumSet;

/**
 * When a mob has no entity target but the Nexus position is recorded on
 * its persistent data (set at spawn time by {@code NexusBlockEntity}),
 * navigate toward the Nexus so the mob participates in the invasion even
 * if the bound player is offline / far. If pathing fails, the mob is
 * pushed physically toward the Nexus so the break-block goal can engage.
 */
public class GoToNexusGoal extends Goal {
    /** Key written on the mob's persistent data by {@code NexusBlockEntity.trySpawn}. */
    public static final String NEXUS_POS_KEY = "invmod:nexusPos";

    private final Mob mob;
    private final double speedModifier;
    private int repathCooldown;

    public GoToNexusGoal(Mob mob, double speedModifier) {
        this.mob = mob;
        this.speedModifier = speedModifier;
        this.setFlags(EnumSet.of(Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        if (mob.getTarget() != null) return false;
        return readNexusPos() != null;
    }

    @Override
    public boolean canContinueToUse() {
        if (!canUse()) return false;
        BlockPos pos = readNexusPos();
        if (pos == null) return false;
        return mob.distanceToSqr(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5) > 4.0;
    }

    @Override
    public void tick() {
        BlockPos pos = readNexusPos();
        if (pos == null) return;

        if (repathCooldown-- <= 0) {
            repathCooldown = 20;
            boolean ok = mob.getNavigation().moveTo(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, speedModifier);
            if (!ok) {
                // Pathing failed — push forward and let the break-block goal handle obstacles.
                mob.getLookControl().setLookAt(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
                mob.getMoveControl().setWantedPosition(pos.getX() + 0.5, mob.getY(), pos.getZ() + 0.5, speedModifier);
            }
        }
    }

    private BlockPos readNexusPos() {
        CompoundTag data = mob.getPersistentData();
        if (!data.contains(NEXUS_POS_KEY, net.minecraft.nbt.Tag.TAG_LIST)) return null;
        net.minecraft.nbt.ListTag list = data.getList(NEXUS_POS_KEY, net.minecraft.nbt.Tag.TAG_INT);
        if (list.size() != 3) return null;
        BlockPos pos = new BlockPos(list.getInt(0), list.getInt(1), list.getInt(2));
        // Validate that the Nexus is still there; if it has been destroyed,
        // drop the goal by removing the data so the mob is "free".
        BlockEntity be = mob.level().getBlockEntity(pos);
        if (!(be instanceof NexusBlockEntity)) {
            data.remove(NEXUS_POS_KEY);
            return null;
        }
        return pos;
    }

}
