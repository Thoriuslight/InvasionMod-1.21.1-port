package invmod.client;

import invmod.InvasionMod;
import invmod.ModEntities;
import invmod.client.model.IMEggModel;
import invmod.client.model.IMImpModel;
import invmod.client.model.IMThrowerModel;
import invmod.client.model.IMVultureModel;
import invmod.client.render.IMCustomRenderers;
import invmod.client.render.IMRenderers;
import invmod.menu.ModMenuTypes;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

@Mod(value = InvasionMod.MODID, dist = Dist.CLIENT)
@EventBusSubscriber(modid = InvasionMod.MODID, value = Dist.CLIENT)
public final class InvasionModClient {
    public InvasionModClient(ModContainer container) {
        container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
    }

    @SubscribeEvent
    static void onClientSetup(FMLClientSetupEvent event) {
        InvasionMod.LOGGER.info("Invasion Mod client setup");
    }

    @SubscribeEvent
    static void registerMenuScreens(net.neoforged.neoforge.client.event.RegisterMenuScreensEvent event) {
        event.register(ModMenuTypes.NEXUS_MENU.get(), invmod.client.screen.NexusScreen::new);
    }

    @SubscribeEvent
    static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ModEntities.IM_ZOMBIE.get(),        IMRenderers.IMZombie::new);
        event.registerEntityRenderer(ModEntities.IM_SKELETON.get(),      IMRenderers.IMSkeleton::new);
        event.registerEntityRenderer(ModEntities.IM_SPIDER.get(),        IMRenderers.IMSpider::new);
        event.registerEntityRenderer(ModEntities.IM_CREEPER.get(),       IMRenderers.IMCreeper::new);
        event.registerEntityRenderer(ModEntities.IM_ZOMBIE_PIGMAN.get(), IMRenderers.IMZombiePigman::new);
        event.registerEntityRenderer(ModEntities.IM_PIGMAN_ENGINEER.get(),      IMRenderers.IMPigEngy::new);
        event.registerEntityRenderer(ModEntities.IM_THROWER.get(),       IMCustomRenderers.IMThrowerRenderer::new);
        event.registerEntityRenderer(ModEntities.IM_BURROWER.get(),      IMCustomRenderers.IMBurrowerRenderer::new);
        event.registerEntityRenderer(ModEntities.IM_WOLF.get(),          IMRenderers.IMWolf::new);
        // Use our custom-built EntityModels for the four shapes that previously
        // looked like miscoloured zombies.
        event.registerEntityRenderer(ModEntities.IM_IMP.get(),           IMCustomRenderers.IMImpRenderer::new);
        event.registerEntityRenderer(ModEntities.IM_BIRD.get(),          ctx -> new IMCustomRenderers.IMVultureRenderer(ctx, 0.45F));
        event.registerEntityRenderer(ModEntities.IM_GIANT_BIRD.get(),    ctx -> new IMCustomRenderers.IMVultureRenderer(ctx, 1.0F));
        event.registerEntityRenderer(ModEntities.IM_EGG.get(),           IMCustomRenderers.IMEggRenderer::new);
        // Projectiles
        event.registerEntityRenderer(ModEntities.IM_BOULDER.get(),
                ctx -> new net.minecraft.client.renderer.entity.ThrownItemRenderer<>(ctx, 2.0F, false));
        event.registerEntityRenderer(ModEntities.IM_BOLT.get(),
                ctx -> new net.minecraft.client.renderer.entity.ThrownItemRenderer<>(ctx));
    }

    @SubscribeEvent
    static void registerLayers(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(IMEggModel.LAYER,             IMEggModel::createBodyLayer);
        event.registerLayerDefinition(IMImpModel.LAYER,             IMImpModel::createBodyLayer);
        event.registerLayerDefinition(IMVultureModel.LAYER_VULTURE, IMVultureModel::createBodyLayer);
        event.registerLayerDefinition(IMThrowerModel.LAYER,         IMThrowerModel::createBodyLayer);
        event.registerLayerDefinition(invmod.client.model.IMBurrowerModel.LAYER, invmod.client.model.IMBurrowerModel::createBodyLayer);
    }
}
