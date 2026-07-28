package invmod.item;

import invmod.InvasionMod;
import invmod.ModRegistry;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModCreativeModeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TAB = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, InvasionMod.MODID);

    public static final Supplier<CreativeModeTab> COMBINE_TAB = CREATIVE_MODE_TAB.register("combine_tab",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.invmod"))
                    .icon(() -> ModItems.NEXUS_CATALYST.get().getDefaultInstance())
                    .displayItems((params, output) -> ModRegistry.populateCreativeTab(output))
                    .build());

    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TAB.register(eventBus);
    }
}
