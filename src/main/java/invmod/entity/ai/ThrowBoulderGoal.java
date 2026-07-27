package invmod.entity.ai;

import invmod.entity.projectile.BoulderEntity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

/** Thrower ranged attack: every N ticks if a target is at medium-range
 *  and visible, hurl a {@link BoulderEntity} at them. */
public class ThrowBoulderGoal extends Goal {
    private static final int COOLDOWN_TICKS = 80;
    private static final double MIN_RANGE_SQ = 4.0 * 4.0;
    private static final double MAX_RANGE_SQ = 24.0 * 24.0;

    private final Mob mob;
    private int cooldown;

    public ThrowBoulderGoal(Mob mob) {
        this.mob = mob;
        this.setFlags(EnumSet.of(Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        LivingEntity t = mob.getTarget();
        if (t == null) return false;
        double d = mob.distanceToSqr(t);
        if (d < MIN_RANGE_SQ || d > MAX_RANGE_SQ) return false;
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
        throwBoulder(t);
    }

    private void throwBoulder(LivingEntity target) {
        BoulderEntity b = new BoulderEntity(mob, mob.level());
        double dx = target.getX() - mob.getX();
        double dy = target.getY(0.4) - b.getY();
        double dz = target.getZ() - mob.getZ();
        double dist = Math.sqrt(dx * dx + dz * dz);
        b.shoot(dx, dy + dist * 0.18, dz, 1.4F, 6.0F);
        mob.swing(net.minecraft.world.InteractionHand.MAIN_HAND);
        mob.playSound(net.minecraft.sounds.SoundEvents.IRON_GOLEM_ATTACK, 1.0F, 0.6F);
        mob.level().addFreshEntity(b);
    }
}
