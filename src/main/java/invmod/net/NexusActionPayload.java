package invmod.net;

import invmod.InvasionMod;
import invmod.block.entity.NexusBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.network.handling.IPayloadContext;

/** C->S payload: player clicked a button in the Nexus screen. */
public record NexusActionPayload(BlockPos pos, int action) implements CustomPacketPayload {
    public static final int ACTION_BEGIN  = 0;
    public static final int ACTION_END    = 1;
    public static final int ACTION_RADIUS = 2;

    public static final CustomPacketPayload.Type<NexusActionPayload> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(InvasionMod.MODID, "nexus_action"));

    public static final StreamCodec<RegistryFriendlyByteBuf, NexusActionPayload> CODEC =
            StreamCodec.of((buf, msg) -> { buf.writeBlockPos(msg.pos); buf.writeVarInt(msg.action); },
                           buf -> new NexusActionPayload(buf.readBlockPos(), buf.readVarInt()));

    @Override public Type<? extends CustomPacketPayload> type() { return TYPE; }

    public static void handle(NexusActionPayload msg, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            Player player = ctx.player();
            if (!(player.level() instanceof ServerLevel level)) return;
            BlockEntity be = level.getBlockEntity(msg.pos);
            if (!(be instanceof NexusBlockEntity nexus)) return;
            if (player.distanceToSqr(msg.pos.getX() + 0.5, msg.pos.getY() + 0.5, msg.pos.getZ() + 0.5) > 64.0) return;
            switch (msg.action) {
                case ACTION_BEGIN  -> { nexus.bindPlayer(player); nexus.startWave(player, level); }
                case ACTION_END    -> nexus.stopWave(level);
                case ACTION_RADIUS -> nexus.cycleSpawnRadius();
                default -> {}
            }
        });
    }
}
