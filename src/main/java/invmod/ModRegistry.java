package invmod;

import invmod.block.ModBlocks;
import invmod.block.entity.ModBlockEntities;
import invmod.item.ModItems;
import net.minecraft.world.item.CreativeModeTab;

public final class ModRegistry {
    private ModRegistry() {}

    static void bootstrap() {
        // Force class-init of every per-domain registrar so its DeferredHolders attach.
        ModEntities.touch();
    }

    public static void populateCreativeTab(CreativeModeTab.Output output) {
        // Block items
        output.accept(ModBlocks.NEXUS);
        // Spawn eggs
        output.accept(ModEntities.IM_ZOMBIE_SPAWN_EGG);
        output.accept(ModEntities.IM_SKELETON_SPAWN_EGG);
        output.accept(ModEntities.IM_SPIDER_SPAWN_EGG);
        output.accept(ModEntities.IM_CREEPER_SPAWN_EGG);
        output.accept(ModEntities.IM_ZOMBIE_PIGMAN_SPAWN_EGG);
        output.accept(ModEntities.IM_PIG_ENGY_SPAWN_EGG);
        output.accept(ModEntities.IM_THROWER_SPAWN_EGG);
        output.accept(ModEntities.IM_BURROWER_SPAWN_EGG);
        output.accept(ModEntities.IM_IMP_SPAWN_EGG);
        output.accept(ModEntities.IM_WOLF_SPAWN_EGG);
        output.accept(ModEntities.IM_BIRD_SPAWN_EGG);
        output.accept(ModEntities.IM_GIANT_BIRD_SPAWN_EGG);
        output.accept(ModEntities.IM_EGG_SPAWN_EGG);
        // Resources
        output.accept(ModItems.PHASE_CRYSTAL);
        output.accept(ModItems.RIFT_FLUX);
        output.accept(ModItems.STRANGE_BONE);
        output.accept(ModItems.SMALL_REMNANTS);
        // Catalysts & mixtures
        output.accept(ModItems.CATALYST_MIXTURE);
        output.accept(ModItems.STABLE_CATALYST_MIXTURE);
        output.accept(ModItems.NEXUS_CATALYST);
        output.accept(ModItems.STABLE_NEXUS_CATALYST);
        output.accept(ModItems.STRONG_CATALYST);
        output.accept(ModItems.DAMPING_AGENT);
        output.accept(ModItems.STRONG_DAMPING_AGENT);
        // Tools / weapons
        output.accept(ModItems.INFUSED_SWORD);
        output.accept(ModItems.SEARING_BOW);
        output.accept(ModItems.ENGY_HAMMER);
        output.accept(ModItems.NEXUS_ADJUSTER);
        output.accept(ModItems.MATERIAL_PROBE);
        // Traps
        output.accept(ModItems.EMPTY_TRAP);
        output.accept(ModItems.RIFT_TRAP);
        output.accept(ModItems.FLAME_TRAP);
        // Debug
        output.accept(ModItems.DEBUG_WAND);
    }
}
