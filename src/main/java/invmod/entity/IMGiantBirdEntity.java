package invmod.entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;

/** Vulture — large flying boss-tier enemy. Inherits flying setup from
 *  {@link IMBirdEntity}, scales up attributes. */
public class IMGiantBirdEntity extends IMBirdEntity {
    public IMGiantBirdEntity(EntityType<? extends IMGiantBirdEntity> type, Level level) {
        super(type, level);
    }
    public static AttributeSupplier.Builder createAttributes() {
        return IMBirdEntity.createAttributes()
                .add(Attributes.MAX_HEALTH, 80.0)
                .add(Attributes.ATTACK_DAMAGE, 9.0)
                .add(Attributes.MOVEMENT_SPEED, 0.22)
                .add(Attributes.FLYING_SPEED, 0.55)
                .add(Attributes.FOLLOW_RANGE, 96.0)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.6);
    }
}
