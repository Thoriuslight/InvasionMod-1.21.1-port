package invmod;

import invmod.block.NexusBlock;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;

public final class ModBlocks {
    private ModBlocks() {}

    public static final DeferredBlock<NexusBlock> NEXUS = InvasionMod.BLOCKS.register(
            "nexus",
            () -> new NexusBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.STONE)
                    .strength(5.5F, 1200.0F)
                    .sound(SoundType.STONE)
                    .lightLevel(state -> Boolean.TRUE.equals(state.getValue(NexusBlock.ACTIVE)) ? 12 : 0)
                    .requiresCorrectToolForDrops()));

    public static final DeferredItem<BlockItem> NEXUS_ITEM = InvasionMod.ITEMS.registerSimpleBlockItem(
            "nexus", NEXUS, new Item.Properties());

    static void touch() {}
}
