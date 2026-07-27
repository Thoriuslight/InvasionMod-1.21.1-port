package invmod.entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.ZombifiedPiglin;
import net.minecraft.world.level.Level;

public class IMZombiePigmanEntity extends ZombifiedPiglin {
    public IMZombiePigmanEntity(EntityType<? extends IMZombiePigmanEntity> type, Level level) { super(type, level); }
    @Override public boolean isSunBurnTick() { return false; }
    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(3, new invmod.entity.ai.BreakBlocksTowardTargetGoal(this, 4.0F));
        this.goalSelector.addGoal(4, new invmod.entity.ai.GoToNexusGoal(this, 1.0));
    }
    public static AttributeSupplier.Builder createAttributes() {
        return ZombifiedPiglin.createAttributes()
                .add(Attributes.MAX_HEALTH, 32.0)
                .add(Attributes.ATTACK_DAMAGE, 6.0)
                .add(Attributes.MOVEMENT_SPEED, 0.27)
                .add(Attributes.FOLLOW_RANGE, 40.0);
    }
}
