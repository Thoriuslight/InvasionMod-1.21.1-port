package invmod;

import invmod.item.DebugWandItem;
import invmod.item.EngyHammerItem;
import invmod.item.InfusedSwordItem;
import invmod.item.ProbeItem;
import invmod.item.SearingBowItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.neoforged.neoforge.registries.DeferredItem;

/**
 * Phase-B port: all original Invasion-Mod items registered as plain {@link Item}
 * with stacking + durability properties matching the 1.7.2 originals.
 * Right-click / use behaviour will be re-attached in a later phase via
 * dedicated subclasses (sword, bow, probe, trap, etc.).
 */
public final class ModItems {
    private ModItems() {}

    private static final DeferredItem<Item> register(String id, Item.Properties props) {
        return InvasionMod.ITEMS.registerSimpleItem(id, props);
    }

    private static Item.Properties single() { return new Item.Properties().stacksTo(1); }
    private static Item.Properties stack()  { return new Item.Properties(); }

    // Simple resource / catalyst items. All single-stack in the original mod
    // (ItemIM constructor calls setMaxStackSize(1)) except small_remnants.
    public static final DeferredItem<Item> PHASE_CRYSTAL          = register("phase_crystal",          single());
    public static final DeferredItem<Item> RIFT_FLUX              = register("rift_flux",              single().rarity(Rarity.UNCOMMON));
    public static final DeferredItem<Item> NEXUS_CATALYST         = register("nexus_catalyst",         single());
    public static final DeferredItem<Item> STABLE_NEXUS_CATALYST  = register("stable_nexus_catalyst",  single());
    public static final DeferredItem<Item> CATALYST_MIXTURE       = register("catalyst_mixture",       single());
    public static final DeferredItem<Item> STABLE_CATALYST_MIXTURE = register("stable_catalyst_mixture", single());
    public static final DeferredItem<Item> DAMPING_AGENT          = register("damping_agent",          single());
    public static final DeferredItem<Item> STRONG_DAMPING_AGENT   = register("strong_damping_agent",   single());
    public static final DeferredItem<Item> STRONG_CATALYST        = register("strong_catalyst",        single());
    public static final DeferredItem<Item> STRANGE_BONE           = register("strange_bone",           single());
    public static final DeferredItem<Item> SMALL_REMNANTS         = register("small_remnants",         stack().stacksTo(64));

    // Tools / weapons
    public static final DeferredItem<InfusedSwordItem> INFUSED_SWORD = InvasionMod.ITEMS.register(
            "infused_sword", () -> new InfusedSwordItem(single().durability(250).rarity(Rarity.RARE)));
    public static final DeferredItem<SearingBowItem> SEARING_BOW = InvasionMod.ITEMS.register(
            "searing_bow", () -> new SearingBowItem(single().durability(384).rarity(Rarity.RARE)));
    public static final DeferredItem<EngyHammerItem> ENGY_HAMMER = InvasionMod.ITEMS.register(
            "engy_hammer", () -> new EngyHammerItem(single().durability(128)));
    public static final DeferredItem<Item> NEXUS_ADJUSTER         = register("nexus_adjuster",         single());
    public static final DeferredItem<ProbeItem> MATERIAL_PROBE = InvasionMod.ITEMS.register(
            "material_probe", () -> new ProbeItem(single()));
    public static final DeferredItem<DebugWandItem> DEBUG_WAND = InvasionMod.ITEMS.register(
            "debug_wand", () -> new DebugWandItem(single().rarity(Rarity.EPIC)));

    // Trap variants — split into 3 distinct items (cleaner than NBT-discriminated 1.7.2 layout)
    public static final DeferredItem<Item> EMPTY_TRAP             = register("empty_trap",             single());
    public static final DeferredItem<Item> RIFT_TRAP              = register("rift_trap",              single());
    public static final DeferredItem<Item> FLAME_TRAP             = register("flame_trap",             single());

    static void touch() {
        // Force class init so static fields register with DeferredItems
    }
}
