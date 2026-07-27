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

/** Lightning bolt projectile shot by IM Skeletons. Ignites + damages on hit. */
public class BoltEntity extends ThrowableItemProjectile {

    public BoltEntity(EntityType<? extends BoltEntity> type, Level level) {
        super(type, level);
    }

    public BoltEntity(LivingEntity shooter, Level level) {
        super(ModEntities.IM_BOLT.get(), shooter, level);
    }

    @Override
    protected Item getDefaultItem() {
        return Items.ARROW;
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        super.onHitEntity(result);
        if (level().isClientSide()) return;
        if (result.getEntity() instanceof LivingEntity hit) {
            DamageSource src = damageSources().thrown(this, getOwner());
            hit.hurt(src, 4.0F);
            hit.setRemainingFireTicks(40);
        }
    }

    @Override
    protected void onHit(HitResult result) {
        super.onHit(result);
        if (!level().isClientSide()) this.discard();
    }
}
