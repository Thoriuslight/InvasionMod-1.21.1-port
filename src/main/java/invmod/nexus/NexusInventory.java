package invmod.nexus;

import invmod.item.ModItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;

public class NexusInventory extends SimpleContainer {
    static final int MAX_FLUX_GENERATION_TIME = 3000;
    static final int MAX_TRAP_COOK_TIME = 1200;

    private int cookTime;
    private int accumulatedFlux;

    public NexusInventory() {
        super(2);
    }

    public int getFluxProgress() {
        return accumulatedFlux;
    }

    public void setFlugProgress(int time) {
        accumulatedFlux = time;
    }

    public int getCookTime() {
        return cookTime;
    }

    public void setCookTime(int time) {
        cookTime = time;
    }

    public void tick(NexusAccess nexus) {
        tickCookTime(nexus, getItem(0), getItem(1));
    }

    public void generateFlux(int increment) {
        accumulatedFlux += increment;
        if (accumulatedFlux >= MAX_FLUX_GENERATION_TIME) {
            ItemStack currentGeneratedItem = getItem(1);
            if (currentGeneratedItem.isEmpty()) {
                setItem(1, ModItems.RIFT_FLUX.get().getDefaultInstance());
                accumulatedFlux -= MAX_FLUX_GENERATION_TIME;
            } else if (currentGeneratedItem.is(ModItems.RIFT_FLUX.get())) {
                currentGeneratedItem.grow(1);
                accumulatedFlux -= MAX_FLUX_GENERATION_TIME;
            }
        }
    }

    private void tickCookTime(NexusAccess nexus, ItemStack firstStack, ItemStack secondStack) {
        if (!firstStack.isEmpty()) {
            if (firstStack.is(ModItems.EMPTY_TRAP.get())) {
                if (cookTime < MAX_TRAP_COOK_TIME) {
                    cookTime += nexus.getMode() == Mode.STOPPED ? 1 : 9;
                } else {
                    if (secondStack.isEmpty()) {
                        setItem(1, ModItems.FLAME_TRAP.get().getDefaultInstance());
                        firstStack.shrink(1);
                        cookTime = 0;
                    } else if (secondStack.is(ModItems.FLAME_TRAP.get()) && secondStack.getCount() < secondStack.getMaxStackSize()) {
                        secondStack.grow(1);
                        firstStack.shrink(1);
                        cookTime = 0;
                    }
                }
            } else if (firstStack.is(ModItems.RIFT_FLUX.get())) {
                if (cookTime < MAX_TRAP_COOK_TIME && nexus.getLevel() >= 10) {
                    cookTime += 5;
                }

                if (cookTime >= MAX_TRAP_COOK_TIME) {
                    if (secondStack.isEmpty()) {
                        setItem(1, ModItems.STRONG_NEXUS_CATALYST.get().getDefaultInstance());
                        firstStack.shrink(1);
                        cookTime = 0;
                    }
                }
            }
        } else {
            cookTime = 0;
        }
    }

    public void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        accumulatedFlux = tag.getInt("accumulatedFlux");
        cookTime = tag.getInt("cookTime");
        fromTag(tag.getList("Items", ListTag.TAG_COMPOUND), registries);
    }

    public CompoundTag saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        tag.putInt("accumulatedFlux", accumulatedFlux);
        tag.putInt("cookTime", cookTime);
        tag.put("Items", createTag(registries));
        return tag;
    }
}