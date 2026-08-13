package invmod.block.entity;

import invmod.InvasionMod;
import invmod.block.ModBlocks;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public final class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, InvasionMod.MODID);

    public static final Supplier<BlockEntityType<NexusBlockEntity>> NEXUS = BLOCK_ENTITIES.register("nexus_be", () -> BlockEntityType.Builder.of(NexusBlockEntity::new, ModBlocks.NEXUS.get())
            .build(null));

    public static void register(IEventBus eventBus){
        BLOCK_ENTITIES.register(eventBus);
    }
}
