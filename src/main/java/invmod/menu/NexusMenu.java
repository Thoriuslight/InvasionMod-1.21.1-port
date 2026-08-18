package invmod.menu;

import invmod.block.ModBlocks;
import invmod.block.entity.NexusBlockEntity;
import invmod.nexus.Mode;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.Nullable;

public class NexusMenu extends AbstractContainerMenu {
    private final ContainerData properties;
    private final ContainerLevelAccess context;

    public NexusMenu(int syncId, Inventory inventory, FriendlyByteBuf extraData) {
        this(syncId, inventory, new SimpleContainer(2), new SimpleContainerData(10), ContainerLevelAccess.NULL);
    }

    public NexusMenu(int containerId, Inventory playerInventory, Container nexusInventory, ContainerData properties, ContainerLevelAccess context) {
        super(ModMenuTypes.NEXUS_MENU.get(), containerId);
        this.context = context;
        this.properties = properties;
        addDataSlots(properties);
        addSlot(new Slot(nexusInventory, 0, 32, 33));
        addSlot(new OutputSlot(nexusInventory, 1, 102, 33));

        // inventory
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                addSlot(new Slot(playerInventory, col + row * 9 + 9, 8 + col * 18, 84 + row * 18));
            }
        }

        // hotbar
        for (int col = 0; col < 9; col++) {
            addSlot(new Slot(playerInventory, col, 8 + col * 18, 142));
        }
    }

    public int getActivationTimer() {
        return properties.get(0);
    }

    public Mode getMode() {
        return Mode.forId(properties.get(1));
    }

    public int getCurrentWave() {
        return properties.get(2);
    }

    public int getLevel() {
        return properties.get(3);
    }

    public int getKills() {
        return properties.get(4);
    }

    public int getSpawnRadius() {
        return properties.get(5);
    }

    public int getGeneration() {
        return properties.get(6);
    }

    public int getPowerLevel() {
        return properties.get(7);
    }

    public int getCookTime() {
        return properties.get(8);
    }

    public boolean isActivating() {
        return properties.get(9) != 0;
    }

    public int getActivationProgressScaled(int i) {
        return getActivationTimer() * i / 400;
    }

    public int getGenerationProgressScaled(int i) {
        return getGeneration() * i / 3000;
    }

    public int getCookProgressScaled(int i) {
        return getCookTime() * i / 1200;
    }

    @Override
    public boolean stillValid(Player entityplayer) {
        return stillValid(context, entityplayer, ModBlocks.NEXUS.get());
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        @Nullable
        Slot slot = slots.get(index);
        if (slot == null || !slot.hasItem()) {
            return ItemStack.EMPTY;
        }

        ItemStack stack = slot.getItem();
        ItemStack remainder = stack.copy();

        if (index == 1) {
            if (!moveItemStackTo(stack, 2, 38, true)) {
                return ItemStack.EMPTY;
            }
        } else if ((index >= 2) && (index < 38)) {
            if (!moveItemStackTo(stack, 0, 1, false)) {
                return ItemStack.EMPTY;
            }
        } else if (!moveItemStackTo(stack, 2, 38, false)) {
            return ItemStack.EMPTY;
        }

        if (stack.isEmpty()) {
            slot.set(ItemStack.EMPTY);
        } else {
            slot.setChanged();
        }

        if (stack.getCount() == remainder.getCount()) {
            return ItemStack.EMPTY;
        }

        slot.onTake(player, stack);
        return remainder;
    }
}