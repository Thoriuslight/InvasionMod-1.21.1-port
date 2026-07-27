package invmod.entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.level.Level;

/** Burrowing miner that tunnels toward the Nexus. Phase-G uses Zombie shell;
 *  ICanDig terrain-modify behaviour restored in Phase F. */
public class IMBurrowerEntity extends Zombie {
    public IMBurrowerEntity(EntityType<? extends IMBurrowerEntity> type, Level level) { super(type, level); }
    @Override public boolean isSunBurnTick() { return false; }
    @Override
    protected void registerGoals() {
        super.registerGoals();
        // Burrower tunnels through almost anything (hardness up to 7).
        this.goalSelector.addGoal(3, new invmod.entity.ai.BreakBlocksTowardTargetGoal(this, 7.0F));
        this.goalSelector.addGoal(4, new invmod.entity.ai.GoToNexusGoal(this, 1.0));
    }
    public static AttributeSupplier.Builder createAttributes() {
        return Zombie.createAttributes()
                .add(Attributes.MAX_HEALTH, 40.0)
                .add(Attributes.ATTACK_DAMAGE, 5.0)
                .add(Attributes.MOVEMENT_SPEED, 0.20)
                .add(Attributes.FOLLOW_RANGE, 40.0);
    }
}
