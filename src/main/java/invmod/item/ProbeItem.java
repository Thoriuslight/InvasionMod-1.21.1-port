package invmod.item;

import invmod.block.entity.NexusBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

/** Right-click any block; if a Nexus Block-Entity is nearby (8-block radius)
 *  prints its full state machine status. */
public class ProbeItem extends Item {
    public ProbeItem(Properties props) { super(props); }

    @Override
    public InteractionResult useOn(UseOnContext ctx) {
        Level level = ctx.getLevel();
        if (level.isClientSide()) return InteractionResult.SUCCESS;
        BlockPos origin = ctx.getClickedPos();
        for (BlockPos p : BlockPos.betweenClosed(origin.offset(-8, -8, -8), origin.offset(8, 8, 8))) {
            BlockEntity be = level.getBlockEntity(p);
            if (be instanceof NexusBlockEntity nexus) {
                if (ctx.getPlayer() != null) ctx.getPlayer().displayClientMessage(Component.literal(
                        "Probe @" + p.toShortString() + " | mode=" + nexus.getMode()
                                + " wave=" + nexus.getWaveNumber()
                                + " spawned=" + nexus.getMobsSpawnedThisWave() + "/" + nexus.getMobsTargetThisWave()
                                + " radius=" + nexus.getSpawnRadius()
                                + " bound=" + nexus.getBoundPlayerName()), false);
                return InteractionResult.CONSUME;
            }
        }
        if (ctx.getPlayer() != null) ctx.getPlayer().displayClientMessage(
                Component.literal("Probe: no Nexus within 8 blocks."), true);
        return InteractionResult.CONSUME;
    }

    @Override
    public boolean isFoil(ItemStack stack) { return true; }
}
