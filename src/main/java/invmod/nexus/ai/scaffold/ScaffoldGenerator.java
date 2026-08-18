package invmod.nexus.ai.scaffold;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import invmod.nexus.ai.AttackerAI;
import org.jetbrains.annotations.Nullable;

public class ScaffoldGenerator {}/*}
    private final ScaffoldNodeFactory nodeFactory;

    public ScaffoldGenerator(AttackerAI scaffoldManager) {
        this.nodeFactory = new ScaffoldNodeFactory(scaffoldManager);
    }

    public List<ScaffoldNode> generateScaffolds(NexusEntity entity) {
        return DynamicPathNodeNavigator.createHeadlessNavigator(entity, 12, pathSource -> {
            return findCheapestScaffolds(entity, pathSource, entity.getBlockPos());
        });
    }

    private List<ScaffoldNode> findCheapestScaffolds(NexusEntity entity, DynamicPathNodeNavigator.PathSupplier pathSupplier, BlockPos pos) {
        BlockPos nexusPos = entity.getNexus().getOrigin();

        @Nullable
        Path basePath = pathSupplier.findPathTo(nodeFactory, pos, nexusPos, 12);
        if (basePath == null) {
            return List.of();
        }
        List<ScaffoldNode> scaffoldPositions = getNodes(entity, basePath);
        PathingDebugger.sendPathToClients(entity.asEntity(), basePath, 2);

        return switch (scaffoldPositions.size()) {
            case 0 -> List.of();
            case 1 -> scaffoldPositions;
            default -> findCheapestReachable(scaffoldPositions, pathSupplier, nexusPos, pos).orElseGet(() -> {
                return findNonTerminating(scaffoldPositions, pathSupplier, nexusPos, pos);
            });
        };
    }

    private Optional<List<ScaffoldNode>> findCheapestReachable(List<ScaffoldNode> nodes, DynamicPathNodeNavigator.PathSupplier pathSupplier, BlockPos nexusPos, BlockPos pos) {
        return nodes.stream()
                .map(node -> new Pair<>(node, pathSupplier.findPathTo(nodeFactory, pos, nexusPos, 12, chunk -> {
                    ScaffoldView.of(chunk).addScaffoldPosition(node.pos());
                })))
                .filter(pair -> isTerminating(pair.getRight(), nexusPos))
                .sorted(Comparator.comparing(pair -> pair.getRight().getEnd().penalizedPathLength))
                .map(Pair::getLeft)
                .findFirst()
                .map(List::of);
    }

    private List<ScaffoldNode> findNonTerminating(List<ScaffoldNode> scaffoldPositions, DynamicPathNodeNavigator.PathSupplier pathSupplier, BlockPos nexusPos, BlockPos pos) {
        List<ScaffoldNode> copy = new ArrayList<>(scaffoldPositions);
        return scaffoldPositions.stream().filter(scaffold -> {
            @Nullable
            Path path = pathSupplier.findPathTo(nodeFactory, pos, nexusPos, 12, chunk -> {
                for (ScaffoldNode segment : copy) {
                    if (segment != scaffold) {
                        ScaffoldView.of(chunk).addScaffoldPosition(segment.pos());
                    }
                }
            });
            return path != null && !isTerminating(path, nexusPos);
        }).toList();
    }

    private boolean isTerminating(@Nullable Path path, BlockPos target) {
        return path != null && path.getEnd() != null && path.getTarget().equals(target);
    }

    private List<ScaffoldNode> getNodes(NexusEntity entity, Path path) {
        List<ScaffoldNode> scaffoldPositions = new ArrayList<>();
        int startHeight = 0;
        for (int i = 0; i < path.getLength(); i++) {
            PathNode node = path.getNode(i);
            if (i == 0) {
                if (ActionablePathNode.getAction(node) == PathAction.SCAFFOLD_UP) {
                    startHeight = node.y - 1;
                }
            } else if (ActionablePathNode.getAction(node) != PathAction.SCAFFOLD_UP) {
                BlockPos pos = new BlockPos(node.previous.x, startHeight, node.previous.z);
                scaffoldPositions.add(new ScaffoldNode(pos, calculateOrientation(entity.asEntity().getWorld(), pos), node.y - startHeight));
            }
        }

        return scaffoldPositions;
    }

    private Direction calculateOrientation(World world, BlockPos pos) {
        int mostBlocks = 0;
        Direction highestDirection = Direction.EAST;
        BlockPos.Mutable mutable = pos.mutableCopy();
        for (Direction offset : Direction.Type.HORIZONTAL) {
            int blockCount = 0;
            for (int height = 0; height < pos.getY(); height++) {
                if (world.getBlockState(mutable.set(pos).move(Direction.UP, height).move(offset)).isFullCube(world, mutable)) {
                    blockCount++;
                }
                if (world.getBlockState(mutable.set(pos).move(Direction.UP, height).move(offset, 2)).isFullCube(world, mutable)) {
                    blockCount++;
                }
            }
            if (blockCount > mostBlocks) {
                highestDirection = offset;
            }
        }
        return highestDirection;
    }
}*/