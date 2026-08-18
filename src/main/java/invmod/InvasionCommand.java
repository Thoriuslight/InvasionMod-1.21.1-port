package invmod;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import invmod.block.entity.NexusBlockEntity;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public final class InvasionCommand {
 /*   private InvasionCommand() {}

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        LiteralArgumentBuilder<CommandSourceStack> root = Commands.literal("invasion")
                .requires(s -> s.hasPermission(2));

        root.then(Commands.literal("begin")
                .then(Commands.argument("wave", IntegerArgumentType.integer(1, 100))
                        .executes(ctx -> begin(ctx.getSource(), IntegerArgumentType.getInteger(ctx, "wave"))))
                .executes(ctx -> begin(ctx.getSource(), 1)));

        root.then(Commands.literal("end")
                .executes(ctx -> end(ctx.getSource())));

        root.then(Commands.literal("status")
                .executes(ctx -> status(ctx.getSource())));

        root.then(Commands.literal("radius")
                .then(Commands.argument("blocks", IntegerArgumentType.integer(8, 128))
                        .executes(ctx -> setRadius(ctx.getSource(), IntegerArgumentType.getInteger(ctx, "blocks")))));

        root.then(Commands.literal("clear")
                .executes(ctx -> clearMobs(ctx.getSource())));

        dispatcher.register(root);
    }

    private static int begin(CommandSourceStack source, int wave) {
        NexusContext nx = nearestNexus(source);
        if (nx == null) { source.sendFailure(Component.literal("No Nexus block within 64 blocks.")); return 0; }
        nx.nexus.bindPlayer(source.getPlayer());
        nx.nexus.startWave(source.getPlayer(), nx.level);
        source.sendSuccess(() -> Component.literal("Invasion started at " + nx.pos.toShortString()), true);
        return 1;
    }

    private static int end(CommandSourceStack source) {
        NexusContext nx = nearestNexus(source);
        if (nx == null) { source.sendFailure(Component.literal("No Nexus block within 64 blocks.")); return 0; }
        nx.nexus.stopWave(nx.level);
        source.sendSuccess(() -> Component.literal("Invasion halted."), true);
        return 1;
    }

    private static int status(CommandSourceStack source) {
        NexusContext nx = nearestNexus(source);
        if (nx == null) { source.sendFailure(Component.literal("No Nexus block within 64 blocks.")); return 0; }
        source.sendSuccess(() -> Component.literal(
                "Nexus@" + nx.pos.toShortString()
                        + " mode=" + nx.nexus.getMode()
                        + " wave=" + nx.nexus.getWaveNumber()
                        + " " + nx.nexus.getMobsSpawnedThisWave() + "/" + nx.nexus.getMobsTargetThisWave()
                        + " radius=" + nx.nexus.getSpawnRadius()
                        + " bound=" + nx.nexus.getBoundPlayerName()), false);
        return 1;
    }

    private static int setRadius(CommandSourceStack source, int blocks) {
        NexusContext nx = nearestNexus(source);
        if (nx == null) { source.sendFailure(Component.literal("No Nexus block within 64 blocks.")); return 0; }
        // blunt set via reflection? No. Use cycleSpawnRadius until close. Or expose a setter.
        while (nx.nexus.getSpawnRadius() != blocks) {
            int prev = nx.nexus.getSpawnRadius();
            nx.nexus.cycleSpawnRadius();
            if (nx.nexus.getSpawnRadius() == prev) break; // safety
            if (Math.abs(nx.nexus.getSpawnRadius() - blocks) < 8) break;
        }
        source.sendSuccess(() -> Component.literal("Nexus radius approx -> " + nx.nexus.getSpawnRadius()), true);
        return 1;
    }

    private static int clearMobs(CommandSourceStack source) {
        if (!(source.getEntity() instanceof ServerPlayer player)) {
            source.sendFailure(Component.literal("Player-only command."));
            return 0;
        }
        ServerLevel level = (ServerLevel) player.level();
        AABB box = player.getBoundingBox().inflate(64);
        List<LivingEntity> hits = level.getEntitiesOfClass(LivingEntity.class, box,
                e -> e.getClass().getName().startsWith("invmod.entity."));
        for (LivingEntity e : hits) e.kill();
        source.sendSuccess(() -> Component.literal("Cleared " + hits.size() + " IM mobs."), true);
        return 1;
    }

    private record NexusContext(ServerLevel level, BlockPos pos, NexusBlockEntity nexus) {}

    @Nullable
    private static NexusContext nearestNexus(CommandSourceStack source) {
        if (!(source.getLevel() instanceof ServerLevel level)) return null;
        BlockPos origin = BlockPos.containing(source.getPosition());
        BlockPos best = null;
        double bestDist = Double.MAX_VALUE;
        for (BlockPos p : BlockPos.betweenClosed(origin.offset(-64, -32, -64), origin.offset(64, 32, 64))) {
            BlockEntity be = level.getBlockEntity(p);
            if (be instanceof NexusBlockEntity) {
                double d = p.distSqr(origin);
                if (d < bestDist) { bestDist = d; best = p.immutable(); }
            }
        }
        if (best == null) return null;
        return new NexusContext(level, best, (NexusBlockEntity) level.getBlockEntity(best));
    }*/
}
