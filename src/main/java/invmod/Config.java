package invmod;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;

/**
 * Common configuration. Mirrors the keys from the original 1.7.2
 * {@code invasion_config.cfg}. Values are read lazily by other systems
 * (block strength, wave size scaling, mob HP scaling).
 */
@EventBusSubscriber(modid = InvasionMod.MODID, bus = EventBusSubscriber.Bus.MOD)
public final class Config {
    private Config() {}

    private static final ModConfigSpec.Builder B = new ModConfigSpec.Builder();

    public static final ModConfigSpec.BooleanValue DEBUG = B
            .comment("Verbose mod logging")
            .define("debug", false);

    public static final ModConfigSpec.BooleanValue DESTRUCTED_BLOCKS_DROP = B
            .comment("Whether blocks broken by IM mobs drop their items")
            .define("destructedBlocksDrop", false);

    public static final ModConfigSpec.IntValue MIN_DAYS_TO_ATTACK = B
            .comment("Lower bound for the random delay before a Nexus auto-triggers a wave")
            .defineInRange("minDaysToAttack", 3, 0, 60);

    public static final ModConfigSpec.IntValue MAX_DAYS_TO_ATTACK = B
            .comment("Upper bound for the random delay before a Nexus auto-triggers a wave")
            .defineInRange("maxDaysToAttack", 7, 0, 60);

    public static final ModConfigSpec.IntValue SPAWN_INTERVAL_TICKS = B
            .comment("Ticks between mob spawns during a wave (lower = faster)")
            .defineInRange("spawnIntervalTicks", 60, 10, 600);

    public static final ModConfigSpec.IntValue COOLDOWN_TICKS = B
            .comment("Ticks between waves (post-clear cooldown)")
            .defineInRange("cooldownTicks", 600, 0, 24000);

    public static final ModConfigSpec.DoubleValue MOB_HEALTH_MULT = B
            .comment("Global multiplier on every invasion mob's MAX_HEALTH attribute")
            .defineInRange("mobHealthMultiplier", 1.0, 0.1, 10.0);

    public static final ModConfigSpec.DoubleValue MOB_DAMAGE_MULT = B
            .comment("Global multiplier on every invasion mob's ATTACK_DAMAGE attribute")
            .defineInRange("mobDamageMultiplier", 1.0, 0.1, 10.0);

    public static final ModConfigSpec.IntValue MAX_WAVE_SIZE = B
            .comment("Hard cap on mobs spawned per wave")
            .defineInRange("maxWaveSize", 40, 1, 500);

    public static final ModConfigSpec.IntValue WAVE_SIZE_LINEAR = B
            .comment("Wave size = waveSizeBase + wave * waveSizeLinear")
            .defineInRange("waveSizeLinear", 2, 0, 50);

    public static final ModConfigSpec.IntValue WAVE_SIZE_BASE = B
            .comment("Base mob count for wave 1 before linear scaling")
            .defineInRange("waveSizeBase", 4, 1, 100);

    public static final ModConfigSpec.BooleanValue UPDATE_MESSAGES_ENABLED = B
            .comment("Show wave progress messages to the bound player")
            .define("updateMessagesEnabled", true);

    public static final ModConfigSpec SPEC = B.build();

    @SubscribeEvent
    public static void onLoad(final ModConfigEvent.Loading event) {
        InvasionMod.LOGGER.info("Invasion Mod config loaded");
    }

    @SubscribeEvent
    public static void onReload(final ModConfigEvent.Reloading event) {
        InvasionMod.LOGGER.info("Invasion Mod config reloaded");
    }
}
