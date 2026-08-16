package invmod;

import net.neoforged.neoforge.common.ModConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

public class InvasionConfig {
    //Define a field to keep the config and spec for later
    public static final InvasionConfig CONFIG;
    public static final ModConfigSpec CONFIG_SPEC;

    private static final int DEFAULT_MIN_CONT_MODE_DAYS = 2;
    private static final int DEFAULT_MAX_CONT_MODE_DAYS = 3;

    public int minContinuousModeDays = DEFAULT_MIN_CONT_MODE_DAYS;
    public int maxContinuousModeDays = DEFAULT_MAX_CONT_MODE_DAYS;

    private InvasionConfig(ModConfigSpec.Builder builder) {
        // Define properties used by the configuration
        // ...
    }

    //CONFIG and CONFIG_SPEC are both built from the same builder, so we use a static block to seperate the properties
    static {
        Pair<InvasionConfig, ModConfigSpec> pair =
                new ModConfigSpec.Builder().configure(InvasionConfig::new);

        //Store the resulting values
        CONFIG = pair.getLeft();
        CONFIG_SPEC = pair.getRight();
    }
}
