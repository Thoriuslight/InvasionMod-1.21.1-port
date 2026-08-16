package invmod.block.entity;

import invmod.InvasionMod;
import invmod.ModEntities;
import invmod.block.NexusBlock;
import invmod.menu.NexusMenu;
import invmod.nexus.Nexus;
import invmod.nexus.NexusAccess;
import invmod.nexus.WorldNexusStorage;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public final class NexusBlockEntity extends BlockEntity implements Container, MenuProvider {
    private static final int[] SLOTS = {0, 1};

    private UUID nexusId = UUID.randomUUID();
    @Nullable
    private Nexus nexus;

    public NexusBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.NEXUS.get(), pos, state);
    }

    public NexusAccess getNexus() {
        if (nexus == null && getLevel() instanceof ServerLevel sw) {
            nexus = WorldNexusStorage.of(sw).getOrCreate(nexusId, getBlockPos());
        }
        return nexus;
    }

    @Override
    public void setLevel(Level level) {
        super.setLevel(level);
        if (nexus != null && nexus.getWorld() != level) {
            nexus = null;
        }
    }

    @Override
    public void setItem(int i, ItemStack stack) {
        if (getNexus() != null) {
            nexus.getHeldItems().setItem(i, stack);
        }
    }

    @Override
    public ItemStack getItem(int i) {
        return getNexus() != null ? nexus.getHeldItems().getItem(i) : ItemStack.EMPTY;
    }

    @Override
    public ItemStack removeItem(int slot, int amount) {
        return getNexus() != null ? nexus.getHeldItems().removeItem(slot, amount) : ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(Player entityplayer) {
        return true;
    }

    @Override
    public boolean isEmpty() {
        return getNexus() != null && nexus.getHeldItems().isEmpty();
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        return getNexus() != null ? nexus.getHeldItems().removeItemNoUpdate(slot) : ItemStack.EMPTY;
    }

    @Override
    public void clearContent() {
        if (getNexus() != null) {
            nexus.getHeldItems().clearContent();
        }
    }

    @Override
    public int getContainerSize() {
        return getNexus() != null ? nexus.getHeldItems().getContainerSize() : 0;
    }

    //@Override
//public int[] getAvailableSlots(Direction side) {
    //    return SLOTS;
   // }

    @Override
    public boolean canPlaceItem(int slot, ItemStack stack) {
        return false;
    }

    @Override
    public boolean canTakeItem(Container target, int slot, ItemStack stack) {
        return true;
    }

    public void tick(ServerLevel world, BlockPos pos, BlockState state) {

    }

    public void discard() {
        if (getLevel() instanceof ServerLevel sl) {
            WorldNexusStorage.of(sl).destroyNexus(nexusId);
        }
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        if (getNexus() == null) {
            return null;
        }
        return new NexusMenu(containerId, playerInventory, this, nexus.getProperties(), ContainerLevelAccess.create(player.level(), getBlockPos()));
    }

    @Override
    public Component getDisplayName() {
        return getBlockState().getBlock().getName();
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        nexusId = tag.getUUID("nexusId");
        nexus = null;
    }

    @Override
    public void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putUUID("nexusId", nexusId);
    }
}
