package invmod.entity.projectile;

import invmod.ModEntities;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.level.Explosion;

/** Heavy boulder hurled by the IM Thrower. Deals impact damage + small
 *  explosion on collision. */
public class BoulderEntity extends ThrowableItemProjectile {

    public BoulderEntity(EntityType<? extends BoulderEntity> type, Level level) {
        super(type, level);
    }

    public BoulderEntity(LivingEntity shooter, Level level) {
        super(ModEntities.IM_BOULDER.get(), shooter, level);
    }

    @Override
    protected Item getDefaultItem() {
        return Items.COBBLESTONE;
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        super.onHitEntity(result);
        if (level().isClientSide()) return;
        if (result.getEntity() instanceof LivingEntity hit) {
            DamageSource src = damageSources().thrown(this, getOwner());
            hit.hurt(src, 8.0F);
        }
    }

    @Override
    protected void onHit(HitResult result) {
        super.onHit(result);
        if (level().isClientSide()) return;
        level().explode(this, getX(), getY(), getZ(), 1.5F,
                Level.ExplosionInteraction.MOB);
        this.discard();
    }
}
