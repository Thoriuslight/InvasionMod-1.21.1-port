package invmod.item;

import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.common.SimpleTier;

public class ModToolTiers {
    public static final Tier INFUSED_GOLD =  new SimpleTier(BlockTags.INCORRECT_FOR_DIAMOND_TOOL, 40, 12, 6, 22, () -> Ingredient.of(Items.DIAMOND));
}
