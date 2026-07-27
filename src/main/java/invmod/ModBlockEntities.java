package invmod;

import invmod.block.entity.NexusBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredHolder;

public final class ModBlockEntities {
    private ModBlockEntities() {}

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<NexusBlockEntity>> NEXUS =
            InvasionMod.BLOCK_ENTITY_TYPES.register(
                    "nexus",
                    () -> BlockEntityType.Builder.of(NexusBlockEntity::new, ModBlocks.NEXUS.get())
                            .build(null));

    static void touch() {}
}
