package invmod.entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.level.Level;

/** Small fast attacker. Phase-G uses Zombie scaffolding so the entity gets
 *  vanilla goals + a free humanoid renderer; original imp model + the
 *  water-damage behaviour are restored in Phase F. */
public class IMImpEntity extends Zombie {
    public IMImpEntity(EntityType<? extends IMImpEntity> type, Level level) { super(type, level); }
    @Override public boolean isSunBurnTick() { return false; }
    public static AttributeSupplier.Builder createAttributes() {
        return Zombie.createAttributes()
                .add(Attributes.MAX_HEALTH, 12.0)
                .add(Attributes.ATTACK_DAMAGE, 3.0)
                .add(Attributes.MOVEMENT_SPEED, 0.40)
                .add(Attributes.FOLLOW_RANGE, 24.0);
    }
    @Override public boolean isBaby() { return true; }
}
