package invmod.nexus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import invmod.util.math.DistanceComparators;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.Nullable;

public class Combatants implements Iterable<Combatant<?>> {
    private List<Combatant<?>> mobList = new ArrayList<>();
    private boolean sorted;

    private final Nexus nexus;
    private final Comparator<Combatant<?>> sorter;

    public Combatants(Nexus nexus) {
        this.nexus = nexus;
        this.sorter = Comparator.comparing(Combatant::asEntity, DistanceComparators.ofComparisonEntities(nexus.getOrigin().getCenter()));
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void updateMobList(AABB arena) {
        mobList = (List)nexus.getWorld().getEntitiesOfClass(PathfinderMob.class, arena, Combatant.PREDICATE);
        sorted = false;
    }

    @Nullable
    public Combatant<?> removeNearestCombatant() {
        if (mobList.isEmpty()) {
            return null;
        }

        if (!sorted) {
            sorted = true;
            Collections.sort(mobList, sorter);
        }
        return mobList.removeLast();
    }

    @Override
    public Iterator<Combatant<?>> iterator() {
        return mobList.iterator();
    }
}