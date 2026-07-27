package invmod.entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.ZombifiedPiglin;
import net.minecraft.world.level.Level;

/** Pigman engineer — carries hammer, dismantles structures around the nexus. */
public class IMPigEngyEntity extends ZombifiedPiglin {
    public IMPigEngyEntity(EntityType<? extends IMPigEngyEntity> type, Level level) { super(type, level); }
    @Override public boolean isSunBurnTick() { return false; }
    @Override
    protected void registerGoals() {
        super.registerGoals();
        // Engineer: build ladder towers up when target is above; dig forward
        // when target is on level or below. Build takes priority (lower number)
        // so the engineer "ramps up" before tunneling.
        this.goalSelector.addGoal(2, new invmod.entity.ai.PlaceLadderTowardTargetGoal(this));
        this.goalSelector.addGoal(3, new invmod.entity.ai.BreakBlocksTowardTargetGoal(this, 2.5F));
        this.goalSelector.addGoal(4, new invmod.entity.ai.GoToNexusGoal(this, 1.0));
    }
    public static AttributeSupplier.Builder createAttributes() {
        return ZombifiedPiglin.createAttributes()
                .add(Attributes.MAX_HEALTH, 18.0)
                .add(Attributes.ATTACK_DAMAGE, 2.0)
                .add(Attributes.MOVEMENT_SPEED, 0.24)
                .add(Attributes.FOLLOW_RANGE, 40.0);
    }
}
