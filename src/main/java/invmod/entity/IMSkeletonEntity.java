package invmod.entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Skeleton;
import net.minecraft.world.level.Level;

public class IMSkeletonEntity extends Skeleton {
    public IMSkeletonEntity(EntityType<? extends IMSkeletonEntity> type, Level level) { super(type, level); }
    @Override public boolean isSunBurnTick() { return false; }
    @Override
    protected void registerGoals() {
        super.registerGoals();
        // Replace vanilla bow with our Bolt-shooting attack.
        this.goalSelector.addGoal(2, new invmod.entity.ai.ShootBoltGoal(this));
        this.goalSelector.addGoal(5, new invmod.entity.ai.GoToNexusGoal(this, 1.0));
    }
    public static AttributeSupplier.Builder createAttributes() {
        return Skeleton.createAttributes()
                .add(Attributes.MAX_HEALTH, 24.0)
                .add(Attributes.ATTACK_DAMAGE, 4.0)
                .add(Attributes.MOVEMENT_SPEED, 0.27)
                .add(Attributes.FOLLOW_RANGE, 40.0);
    }
}
