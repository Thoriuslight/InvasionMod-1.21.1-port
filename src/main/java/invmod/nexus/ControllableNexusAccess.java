package invmod.nexus;


public interface ControllableNexusAccess extends NexusAccess {

    boolean start(int wave);

    void stop(boolean killEnemies);

    boolean setSpawnRadius(int radius);
}