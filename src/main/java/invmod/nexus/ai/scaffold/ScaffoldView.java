package invmod.nexus.ai.scaffold;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;

public interface ScaffoldView {
    int MOB_DENSITY_FLAG = 0x7;
    int EXT_DATA_SCAFFOLD_METAPOSITION = 1 << 14;

    ScaffoldView DEFAULT = new ScaffoldView() {
        @Override
        public void setData(BlockPos pos, int data) {
        }

        @Override
        public int getData(BlockPos pos) {
            return 0;
        }
    };

    void setData(BlockPos pos, int data);

    int getData(BlockPos pos);

    default int getMobDensity(BlockPos pos) {
        return getData(pos) & MOB_DENSITY_FLAG;
    }

    default boolean isScaffoldPosition(BlockPos pos) {
        return (getData(pos) & EXT_DATA_SCAFFOLD_METAPOSITION) != 0;
    }

    default void addScaffoldPosition(BlockPos pos) {
        setData(pos, getData(pos) | EXT_DATA_SCAFFOLD_METAPOSITION);
    }

    static ScaffoldView of(BlockGetter view) {
        return view instanceof ScaffoldView v ? v : DEFAULT;
    }
}