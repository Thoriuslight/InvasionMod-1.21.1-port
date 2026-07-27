package invmod.item;

import invmod.entity.IMZombieEntity;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

import java.util.List;

/** Admin tool: right-click in air kills every Invasion-Mod entity within
 *  64 blocks of the player. Useful for testing and resetting waves. */
public class DebugWandItem extends Item {
    public DebugWandItem(Properties props) { super(props); }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, net.minecraft.world.entity.player.Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (level.isClientSide()) return InteractionResultHolder.success(stack);
        AABB box = player.getBoundingBox().inflate(64);
        List<LivingEntity> hits = level.getEntitiesOfClass(LivingEntity.class, box,
                e -> e.getClass().getName().startsWith("invmod.entity."));
        for (LivingEntity e : hits) e.kill();
        player.displayClientMessage(Component.literal("DebugWand: cleared " + hits.size() + " IM entities."), false);
        return InteractionResultHolder.success(stack);
    }

    @Override
    public boolean isFoil(ItemStack stack) { return true; }
}
