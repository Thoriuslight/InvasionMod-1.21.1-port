package invmod.nexus;

import java.util.List;
import java.util.UUID;

import invmod.nexus.ai.AttackerAI;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

public interface NexusAccess {
    long BIND_EXPIRE_TIME = 300000L;
    long TICKS_PER_DAY = Level.TICKS_PER_DAY;//24000
    long SUNSET_TIME = 12000L;
    long HALF_DAY_TIME = 14000L;
    long NIGHT_TIME = 16000L;

    int WAVE_DURATION = 240;

    UUID getUuid();

    boolean isDiscarded();

    BlockPos getOrigin();

    boolean isActivating();

    Mode getMode();

    int getLevel();

    int getSpawnRadius();

    int getCurrentWave();

    Level getWorld();

    AttackerAI getAttackerAI();

    Participants getParticipants();

    boolean isActive();

    List<Component> getStatus();

    void notifyCombatantRemoved(Combatant<?> combatant, Entity.RemovalReason reason);

    void damage(DamageSource source, int amount);
}