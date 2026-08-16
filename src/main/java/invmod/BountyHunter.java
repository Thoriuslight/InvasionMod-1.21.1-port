package invmod;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.UUIDUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.saveddata.SavedData;

/**
 * Cleans up players that somehow get away
 *
 * If the player is not in the world at the time the nexus is destroyed/closed
 * they get sent to the hunter to be killed once they return.
 */
public class BountyHunter extends SavedData {
    private static final int TICK_RATE = 3500;
    private static final Codec<List<UUID>> DEATH_LIST_CODEC = UUIDUtil.CODEC.listOf();
    private static final ResourceLocation ID = InvasionMod.id("nexus_bounty_hunter");

    public static Factory<BountyHunter> getType(ServerLevel level) {
        return new SavedData.Factory<>(() -> new BountyHunter(level), (nbt, lookup) -> new BountyHunter(level, nbt, lookup), DataFixTypes.LEVEL);
    }

    public static BountyHunter of(ServerLevel level) {
        return level.getDataStorage().get(getType(level), ID.toLanguageKey());
    }

    private final List<UUID> players = new ArrayList<>();
    private long time;

    private final ServerLevel level;

    private BountyHunter(ServerLevel level) {
        this.level = level;
    }

    private BountyHunter(ServerLevel level, CompoundTag tag, HolderLookup.Provider registries) {
        this(level);
        DEATH_LIST_CODEC.decode(NbtOps.INSTANCE, tag.get("players")).result().map(Pair::getFirst).ifPresent(players::addAll);
    }

    @Override
    public CompoundTag save(CompoundTag tag, HolderLookup.Provider registries) {
        DEATH_LIST_CODEC.encodeStart(NbtOps.INSTANCE, players).result().ifPresent(d -> tag.put("players", d));
        return tag;
    }

    public void tick() {
        if (players.isEmpty()) {
            return;
        }

        setDirty();

        if (++time % TICK_RATE == 0) {
            for (UUID id : players) {
                Player player = level.getPlayerByUUID(id);
                if (player != null) {
                    players.remove(id);
                    player.hurt(level.damageSources().magic(), 500);
                    player.setHealth(1);
                   // level.getServer().getPlayerList().broadcastAll(new GameMessageS2CPacket(Component.literal("Nexus energies caught up to ").append(player.getDisplayName()), false));
                    setDirty();
                }
            }
        }
    }

    public void add(UUID playerId) {
        players.add(playerId);
        setDirty();
    }

}