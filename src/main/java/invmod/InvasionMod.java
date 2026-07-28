package invmod;

import com.mojang.logging.LogUtils;
import invmod.block.ModBlocks;
import invmod.item.ModCreativeModeTabs;
import invmod.item.ModItems;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.slf4j.Logger;

@Mod(InvasionMod.MODID)
public final class InvasionMod {
    public static final String MODID = "invmod";
    public static final Logger LOGGER = LogUtils.getLogger();

    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(Registries.ENTITY_TYPE, MODID);
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, MODID);
    public static final DeferredRegister<MenuType<?>> MENU_TYPES = DeferredRegister.create(Registries.MENU, MODID);
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);

    public InvasionMod(IEventBus modBus, ModContainer container) {
        LOGGER.info("Invasion Mod port skeleton loading");

        NeoForge.EVENT_BUS.register(this);

        ModCreativeModeTabs.register(modBus);

        ModItems.register(modBus);
        ModBlocks.register(modBus);

        container.registerConfig(net.neoforged.fml.config.ModConfig.Type.COMMON, Config.SPEC);

        ModRegistry.bootstrap();

        ENTITY_TYPES.register(modBus);
        BLOCK_ENTITY_TYPES.register(modBus);
        MENU_TYPES.register(modBus);
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
    }
}
