package invmod.menu;

import invmod.block.entity.NexusBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.Nullable;

/**
 * Slotless menu that proxies the {@link NexusBlockEntity} state machine into
 * a synced {@link ContainerData} (5 ints: mode, wave, spawned, target, radius)
 * so the screen can display live status. Player actions (begin/end/radius)
 * are sent via {@link invmod.net.NexusActionPayload} rather than menu slots.
 */
public class NexusMenu extends AbstractContainerMenu {
    public static final int DATA_MODE     = 0;
    public static final int DATA_WAVE     = 1;
    public static final int DATA_SPAWNED  = 2;
    public static final int DATA_TARGET   = 3;
    public static final int DATA_RADIUS   = 4;
    private static final int DATA_SIZE    = 5;

   // private final ContainerData data;
    //private final @Nullable BlockPos nexusPos;

    /** Client-side ctor invoked by NeoForge's menu opener. */
    public NexusMenu(int id, Inventory inv, BlockEntity blockentity) {
        super(ModMenuTypes.NEXUS_MENU.get(), id);
        //this.nexusPos = pos;
        //this.data = data;
        addDataSlots(data);
    }

    public int getMode()         { return data.get(DATA_MODE); }
    public int getWaveNumber()   { return data.get(DATA_WAVE); }
    public int getSpawned()      { return data.get(DATA_SPAWNED); }
    public int getTarget()       { return data.get(DATA_TARGET); }
    public int getRadius()       { return data.get(DATA_RADIUS); }
    public @Nullable BlockPos getNexusPos() { return nexusPos; }

    @Override
    public ItemStack quickMoveStack(Player player, int index) { return ItemStack.EMPTY; }

    @Override
    public boolean stillValid(Player player) {
        if (nexusPos == null) return false;
        Level lvl = player.level();
        BlockEntity be = lvl.getBlockEntity(nexusPos);
        return be instanceof NexusBlockEntity && player.distanceToSqr(
                nexusPos.getX() + 0.5, nexusPos.getY() + 0.5, nexusPos.getZ() + 0.5) <= 64.0;
    }
}
