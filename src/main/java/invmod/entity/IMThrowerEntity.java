package invmod.entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.level.Level;

/** Larger zombie variant that hurls boulders at distant targets. Phase-G uses
 *  Zombie scaffolding; ranged-attack Goal added in Phase F. */
public class IMThrowerEntity extends Zombie {
    public IMThrowerEntity(EntityType<? extends IMThrowerEntity> type, Level level) { super(type, level); }
    @Override public boolean isSunBurnTick() { return false; }
    @Override
    protected void registerGoals() {
        super.registerGoals();
        // Ranged attack takes priority — Thrower's defining ability.
        this.goalSelector.addGoal(2, new invmod.entity.ai.ThrowBoulderGoal(this));
        // Thrower smashes through stone-tier blocks with brute force.
        this.goalSelector.addGoal(3, new invmod.entity.ai.BreakBlocksTowardTargetGoal(this, 3.0F));
        this.goalSelector.addGoal(4, new invmod.entity.ai.GoToNexusGoal(this, 1.0));
    }
    public static AttributeSupplier.Builder createAttributes() {
        return Zombie.createAttributes()
                .add(Attributes.MAX_HEALTH, 60.0)
                .add(Attributes.ATTACK_DAMAGE, 8.0)
                .add(Attributes.MOVEMENT_SPEED, 0.22)
                .add(Attributes.FOLLOW_RANGE, 40.0)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.5);
    }
}
