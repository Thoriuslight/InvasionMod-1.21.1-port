package invmod.entity.ai;

import invmod.entity.projectile.BoltEntity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.EnumSet;

/** IM Skeleton ranged attack — fires a fast {@link BoltEntity} at the
 *  target every N ticks when in line of sight. */
public class ShootBoltGoal extends Goal {
    private static final int COOLDOWN_TICKS = 40;
    private static final double MAX_RANGE_SQ = 20.0 * 20.0;

    private final Mob mob;
    private int cooldown;

    public ShootBoltGoal(Mob mob) {
        this.mob = mob;
        this.setFlags(EnumSet.of(Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        LivingEntity t = mob.getTarget();
        if (t == null || mob.distanceToSqr(t) > MAX_RANGE_SQ) return false;
        return mob.getSensing().hasLineOfSight(t);
    }

    @Override
    public boolean canContinueToUse() { return canUse(); }

    @Override
    public void tick() {
        LivingEntity t = mob.getTarget();
        if (t == null) return;
        mob.getLookControl().setLookAt(t, 30F, 30F);
        if (--cooldown > 0) return;
        cooldown = COOLDOWN_TICKS;
        shoot(t);
    }

    private void shoot(LivingEntity target) {
        BoltEntity b = new BoltEntity(mob, mob.level());
        double dx = target.getX() - mob.getX();
        double dy = target.getY(0.5) - b.getY();
        double dz = target.getZ() - mob.getZ();
        double dist = Math.sqrt(dx * dx + dz * dz);
        b.shoot(dx, dy + dist * 0.08, dz, 2.0F, 1.5F);
        mob.swing(net.minecraft.world.InteractionHand.MAIN_HAND);
        mob.playSound(net.minecraft.sounds.SoundEvents.ARROW_SHOOT, 1.0F, 1.0F);
        mob.level().addFreshEntity(b);
    }
}
