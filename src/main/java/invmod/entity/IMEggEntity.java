package invmod.entity;

import invmod.ModEntities;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.level.Level;

/**
 * Spider egg — stationary entity that hatches into an {@link IMSpiderEntity}
 * after {@link #HATCH_TICKS} ticks. Movement zeroed so the egg never wanders
 * away from its spawn position.
 */
public class IMEggEntity extends Zombie {
    private static final int HATCH_TICKS = 600;  // ~30 seconds

    private int hatchTimer;

    public IMEggEntity(EntityType<? extends IMEggEntity> type, Level level) {
        super(type, level);
    }

    @Override public boolean isSunBurnTick() { return false; }

    public static AttributeSupplier.Builder createAttributes() {
        return Zombie.createAttributes()
                .add(Attributes.MAX_HEALTH, 6.0)
                .add(Attributes.ATTACK_DAMAGE, 0.0)
                .add(Attributes.MOVEMENT_SPEED, 0.0)
                .add(Attributes.FOLLOW_RANGE, 0.0);
    }

    @Override
    protected void registerGoals() {
        // No goals — egg is stationary until hatch.
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (!(level() instanceof ServerLevel server)) return;
        if (++hatchTimer < HATCH_TICKS) return;
        hatch(server);
    }

    private void hatch(ServerLevel server) {
        IMSpiderEntity spider = ModEntities.IM_SPIDER.get().create(server);
        if (spider != null) {
            spider.moveTo(getX(), getY(), getZ(), getYRot(), 0F);
            spider.finalizeSpawn(server, server.getCurrentDifficultyAt(blockPosition()),
                    MobSpawnType.MOB_SUMMONED, null);
            server.addFreshEntity(spider);
            // Carry the Nexus binding from egg to spider so the new spider
            // joins the invasion stream.
            if (this.getPersistentData().contains(invmod.entity.ai.GoToNexusGoal.NEXUS_POS_KEY)) {
                spider.getPersistentData().put(invmod.entity.ai.GoToNexusGoal.NEXUS_POS_KEY,
                        this.getPersistentData().get(invmod.entity.ai.GoToNexusGoal.NEXUS_POS_KEY));
            }
        }
        this.discard();
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("hatchTimer", hatchTimer);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.hatchTimer = tag.getInt("hatchTimer");
    }
}
