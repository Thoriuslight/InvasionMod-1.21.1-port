package invmod.nexus.ai.scaffold;

public class ScaffoldNodeFactory {}/*implements DynamicPathNodeNavigator.NodeFactory {
    private static final int MIN_SCAFFOLD_HEIGHT = 4;

    private final ScaffoldList scaffolds;
    private final int minDistance;

    public ScaffoldNodeFactory(AttackerAI scaffoldManager) {
        scaffolds = scaffoldManager.getScaffolds();
        minDistance = scaffoldManager.getScaffoldSpacing();
    }

    @Override
    public float getDistancePenalty(PathNode previousNode, PathNode nextNode, CollisionView world) {
        PathAction action = ActionablePathNode.getAction(nextNode);
        PathAction prevAction = ActionablePathNode.getAction(previousNode);
        BlockState state = world.getBlockState(nextNode.getBlockPos());
        float materialMultiplier = state.isSolidBlock(world, nextNode.getBlockPos()) ? 2.2F : 1.0F;
        if (action == PathAction.SCAFFOLD_UP) {
            if (prevAction != PathAction.SCAFFOLD_UP) {
                materialMultiplier *= 3.4F;
            }
            return 0.85F * materialMultiplier;
        }
        if (action == PathAction.BRIDGE) {
            if (prevAction == PathAction.SCAFFOLD_UP) {
                materialMultiplier = 0;
            }
            return 1.1F * materialMultiplier;
        }
        if (action.getType() == PathAction.Type.LADDER && action.isHorizontal()) {
            return 1.5F * materialMultiplier;
        }

        return 1;
    }

    @Override
    public int getSuccessors(int index, PathNode[] successors, PathNode node, CollisionView world, DynamicPathNodeNavigator.NodeCache nodeCache) {
        BlockPos pos = node.getBlockPos();
        BlockPos positionAbove = pos.up();
        BlockState stateAbove = world.getBlockState(positionAbove);
        if (ActionablePathNode.getAction(node.previous) == PathAction.SCAFFOLD_UP && !nodeCache.avoidsBlock(world, positionAbove, stateAbove)) {
            PathNode n = nodeCache.getNode(node.x, node.y + 1, node.z, PathAction.SCAFFOLD_UP);
            if (!n.visited) {
                n.type = PathNodeType.WALKABLE;
                n.penalty = n.type.getDefaultPenalty();
                successors[index++] = n;
            }
            return index;
        }

        for (int sl = scaffolds.size() - 1; sl >= 0; sl--) {
            if (scaffolds.get(sl).getNode().pos().isWithinDistance(pos, minDistance)) {
                return index;
            }
        }

        if (stateAbove.isAir()) {
            BlockPos.Mutable mutable = pos.mutableCopy();
            if (world.getBlockState(mutable.move(Direction.DOWN, 2)).isSolidBlock(world, mutable)) {
                for (int i = 1; i < MIN_SCAFFOLD_HEIGHT; i++) {
                    if (world.getBlockState(mutable.set(pos).move(Direction.UP, i)).isAir()) {
                        return index;
                    }
                }

                PathNode n = nodeCache.getNode(node.x, node.y + 1, node.z, PathAction.SCAFFOLD_UP);
                if (!n.visited) {
                    n.type = PathNodeType.WALKABLE;
                    n.penalty = n.type.getDefaultPenalty();
                    successors[index++] = n;
                }
            }
        }

        return index;
    }
}*/