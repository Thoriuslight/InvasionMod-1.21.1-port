package invmod.item;

import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tiers;

/** Iron-tier sword baseline. NBT timer mechanic deferred. */
public class InfusedSwordItem extends SwordItem {
    public InfusedSwordItem(Properties props) {
        super(Tiers.IRON, props.attributes(SwordItem.createAttributes(Tiers.IRON, 4, -2.4F)));
    }
}
