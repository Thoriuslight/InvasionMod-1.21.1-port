package invmod.entity;

import invmod.entity.ai.BreakBlocksTowardTargetGoal;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.level.Level;

/**
 * Phase-D port skeleton of the original {@code EntityIMZombie}. Reuses vanilla
 * Zombie scaffolding (AI/sounds/baby logic) so we get a working, rendered mob
 * with one EntityType registration. Custom AI tasks + invasion-tier scaling
 * are layered back on in Phase F.
 */
public class IMZombieEntity extends Zombie {

    public IMZombieEntity(EntityType<? extends IMZombieEntity> type, Level level) {
        super(type, level);
    }

    /** Original mod sets burnsInDay=false for invasion-spawned mobs. */
    @Override
    public boolean isSunBurnTick() { return false; }

    /** IM Zombie implements ICanDig in original. Adds a break-block goal so it
     *  can tunnel through dirt + soft stone toward the target, and a
     *  GoToNexus goal so it converges on the Nexus when no player is in range. */
    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(3, new BreakBlocksTowardTargetGoal(this, 2.5F));
        this.goalSelector.addGoal(4, new invmod.entity.ai.GoToNexusGoal(this, 1.0));
    }

    /**
     * Attribute baseline for the T1 invasion zombie. Original config exposed
     * separate health values for invasion-spawned vs night-spawned variants;
     * Phase D uses the invasion-tier baseline (health 28, attack 4) and Phase
     * K wires this up to {@code ModConfigSpec}.
     */
    public static AttributeSupplier.Builder createAttributes() {
        return Zombie.createAttributes()
                .add(Attributes.MAX_HEALTH,         28.0)
                .add(Attributes.ATTACK_DAMAGE,       4.0)
                .add(Attributes.MOVEMENT_SPEED,      0.27)
                .add(Attributes.FOLLOW_RANGE,       40.0)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.05);
    }
}
