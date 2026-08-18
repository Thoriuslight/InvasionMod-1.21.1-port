package invmod.nexus.ai.scaffold;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import invmod.nexus.NexusAccess;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class ScaffoldList implements Iterable<Scaffold> {
    private List<Scaffold> entries = new ArrayList<>();

    public void load(List<Scaffold> entries) {
        this.entries.clear();
        this.entries.addAll(entries);
    }

    public Scaffold get(int index) {
        return entries.get(index);
    }

    public Optional<BlockPos> getNearest(BlockPos pos) {
        return entries.stream()
                .map(i -> i.getNode().pos())
                .sorted(Comparator.comparing(pos::distManhattan))
                .findFirst();
    }

    @Nullable
    public Scaffold getAt(BlockPos pos) {
        return entries.stream()
                .filter(scaffold -> scaffold.getNode().contains(pos))
                .findFirst()
                .orElse(null);
    }

    public void tick(Level level) {
        entries.removeIf(scaffold -> {
            Vec3 pos = scaffold.getNode().pos().getCenter();
            level.addParticle(ParticleTypes.HEART, pos.x, pos.y, pos.z, 0.5D, 0.5D, 0.5D);

            return scaffold.updateStatus();
        });
    }

    public int size() {
        return entries.size();
    }

    public boolean addAll(NexusAccess nexus, List<ScaffoldNode> newScaffolds) {
        newScaffolds = new ArrayList<>(newScaffolds);
        boolean changed = newScaffolds.removeIf(newScaffold -> {
            return entries.stream().anyMatch(existingScaffold -> existingScaffold.merge(newScaffold));
        });
        changed |= entries.addAll(newScaffolds.stream().map(node -> new Scaffold(node, nexus)).toList());
        return changed;
    }

    @Override
    public Iterator<Scaffold> iterator() {
        return entries.iterator();
    }
}