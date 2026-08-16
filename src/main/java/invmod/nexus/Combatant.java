package invmod.nexus;

import invmod.InvasionMod;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;

import java.util.function.Predicate;

public interface Combatant<T extends LivingEntity> extends IHasNexus {
    Predicate<Entity> PREDICATE = EntitySelector.LIVING_ENTITY_STILL_ALIVE.and(EntitySelector.NO_CREATIVE_OR_SPECTATOR).and(i -> i instanceof Combatant);

    @Deprecated
    String getLegacyName();

    T asEntity();

    default void resetHealth() {
        T self = asEntity();
        float health = 20.F;//InvasionMod.getConfig().getHealth(this);
        self.getAttribute(Attributes.MAX_HEALTH).setBaseValue(health);
        self.setHealth(health);
    }

    @Override
    default double findDistanceToNexus() {
        if (!hasNexus()) {
            return Double.MAX_VALUE;
        }
        return Math.sqrt(asEntity().distanceToSqr(getNexus().getOrigin().getCenter()));
    }
}