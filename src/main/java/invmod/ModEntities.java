package invmod;

import invmod.entity.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.common.DeferredSpawnEggItem;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;

import java.util.function.Supplier;

public final class ModEntities {
    private ModEntities() {}

    private static <E extends net.minecraft.world.entity.Entity> DeferredHolder<EntityType<?>, EntityType<E>> reg(
            String id, MobCategory cat, EntityType.EntityFactory<E> factory, float w, float h) {
        return InvasionMod.ENTITY_TYPES.register(id,
                () -> EntityType.Builder.of(factory, cat)
                        .sized(w, h)
                        .clientTrackingRange(8)
                        .build(ResourceLocation.fromNamespaceAndPath(InvasionMod.MODID, id).toString()));
    }

    private static DeferredItem<Item> egg(String id, Supplier<? extends EntityType<? extends net.minecraft.world.entity.Mob>> type, int primary, int secondary) {
        return InvasionMod.ITEMS.register(id, () -> new DeferredSpawnEggItem(type, primary, secondary, new Item.Properties()));
    }

    public static final DeferredHolder<EntityType<?>, EntityType<IMZombieEntity>> IM_ZOMBIE =
            reg("im_zombie",        MobCategory.MONSTER, IMZombieEntity::new,        0.6f, 1.95f);
    public static final DeferredHolder<EntityType<?>, EntityType<IMSkeletonEntity>> IM_SKELETON =
            reg("im_skeleton",      MobCategory.MONSTER, IMSkeletonEntity::new,      0.6f, 1.99f);
    public static final DeferredHolder<EntityType<?>, EntityType<IMSpiderEntity>> IM_SPIDER =
            reg("im_spider",        MobCategory.MONSTER, IMSpiderEntity::new,        1.4f, 0.9f);
    public static final DeferredHolder<EntityType<?>, EntityType<IMCreeperEntity>> IM_CREEPER =
            reg("im_creeper",       MobCategory.MONSTER, IMCreeperEntity::new,       0.6f, 1.7f);
    public static final DeferredHolder<EntityType<?>, EntityType<IMZombiePigmanEntity>> IM_ZOMBIE_PIGMAN =
            reg("im_zombie_pigman", MobCategory.MONSTER, IMZombiePigmanEntity::new,  0.6f, 1.95f);
    public static final DeferredHolder<EntityType<?>, EntityType<IMPigEngyEntity>> IM_PIG_ENGY =
            reg("im_pig_engy",      MobCategory.MONSTER, IMPigEngyEntity::new,       0.6f, 1.95f);
    public static final DeferredHolder<EntityType<?>, EntityType<IMThrowerEntity>> IM_THROWER =
            reg("im_thrower",       MobCategory.MONSTER, IMThrowerEntity::new,       0.9f, 2.5f);
    public static final DeferredHolder<EntityType<?>, EntityType<IMBurrowerEntity>> IM_BURROWER =
            reg("im_burrower",      MobCategory.MONSTER, IMBurrowerEntity::new,      0.6f, 1.95f);
    public static final DeferredHolder<EntityType<?>, EntityType<IMImpEntity>> IM_IMP =
            reg("im_imp",           MobCategory.MONSTER, IMImpEntity::new,           0.5f, 1.0f);
    public static final DeferredHolder<EntityType<?>, EntityType<IMWolfEntity>> IM_WOLF =
            reg("im_wolf",          MobCategory.CREATURE, IMWolfEntity::new,         0.6f, 0.85f);
    public static final DeferredHolder<EntityType<?>, EntityType<IMBirdEntity>> IM_BIRD =
            reg("im_bird",          MobCategory.MONSTER, IMBirdEntity::new,          0.5f, 1.0f);
    public static final DeferredHolder<EntityType<?>, EntityType<IMGiantBirdEntity>> IM_GIANT_BIRD =
            reg("im_giant_bird",    MobCategory.MONSTER, IMGiantBirdEntity::new,     1.5f, 2.5f);
    public static final DeferredHolder<EntityType<?>, EntityType<IMEggEntity>> IM_EGG =
            reg("im_egg",           MobCategory.MONSTER, IMEggEntity::new,           0.5f, 0.5f);

    // ---- Projectiles ----
    public static final DeferredHolder<EntityType<?>, EntityType<invmod.entity.projectile.BoulderEntity>> IM_BOULDER =
            reg("im_boulder",       MobCategory.MISC, invmod.entity.projectile.BoulderEntity::new, 0.75f, 0.75f);
    public static final DeferredHolder<EntityType<?>, EntityType<invmod.entity.projectile.BoltEntity>> IM_BOLT =
            reg("im_bolt",          MobCategory.MISC, invmod.entity.projectile.BoltEntity::new,    0.3f, 0.3f);

    // Spawn eggs (display-only colors; tweak in Phase K from textures)
    public static final DeferredItem<Item> IM_ZOMBIE_SPAWN_EGG        = egg("im_zombie_spawn_egg",        IM_ZOMBIE,        0x004C00, 0x799C65);
    public static final DeferredItem<Item> IM_SKELETON_SPAWN_EGG      = egg("im_skeleton_spawn_egg",      IM_SKELETON,      0xC1C1C1, 0x494949);
    public static final DeferredItem<Item> IM_SPIDER_SPAWN_EGG        = egg("im_spider_spawn_egg",        IM_SPIDER,        0x342D27, 0xA80E0E);
    public static final DeferredItem<Item> IM_CREEPER_SPAWN_EGG       = egg("im_creeper_spawn_egg",       IM_CREEPER,       0x0DA70B, 0x000000);
    public static final DeferredItem<Item> IM_ZOMBIE_PIGMAN_SPAWN_EGG = egg("im_zombie_pigman_spawn_egg", IM_ZOMBIE_PIGMAN, 0xEA9393, 0x4C7129);
    public static final DeferredItem<Item> IM_PIG_ENGY_SPAWN_EGG      = egg("im_pig_engy_spawn_egg",      IM_PIG_ENGY,      0xC09090, 0x947050);
    public static final DeferredItem<Item> IM_THROWER_SPAWN_EGG       = egg("im_thrower_spawn_egg",       IM_THROWER,       0x223300, 0x666600);
    public static final DeferredItem<Item> IM_BURROWER_SPAWN_EGG      = egg("im_burrower_spawn_egg",      IM_BURROWER,      0x553311, 0x8B5A2B);
    public static final DeferredItem<Item> IM_IMP_SPAWN_EGG           = egg("im_imp_spawn_egg",           IM_IMP,           0xFF3300, 0xFFCC00);
    public static final DeferredItem<Item> IM_WOLF_SPAWN_EGG          = egg("im_wolf_spawn_egg",          IM_WOLF,          0xD7D3D3, 0xCCAA77);
    public static final DeferredItem<Item> IM_BIRD_SPAWN_EGG          = egg("im_bird_spawn_egg",          IM_BIRD,          0x222222, 0x111111);
    public static final DeferredItem<Item> IM_GIANT_BIRD_SPAWN_EGG    = egg("im_giant_bird_spawn_egg",    IM_GIANT_BIRD,    0x554433, 0x110000);
    public static final DeferredItem<Item> IM_EGG_SPAWN_EGG           = egg("im_egg_spawn_egg",           IM_EGG,           0xA8C0C0, 0x342D27);

    static void touch() {}
}
