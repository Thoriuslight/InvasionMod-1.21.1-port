package invmod;

import invmod.entity.*;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;

public final class ModEvents {
    private ModEvents() {}

    @EventBusSubscriber(modid = InvasionMod.MODID)
    public static final class ModBus {
        @SubscribeEvent
        public static void onAttributeCreation(EntityAttributeCreationEvent event) {
        event.put(ModEntities.IM_ZOMBIE.get(),        IMZombieEntity.createAttributes().build());
        event.put(ModEntities.IM_SKELETON.get(),      IMSkeletonEntity.createAttributes().build());
        event.put(ModEntities.IM_SPIDER.get(),        IMSpiderEntity.createAttributes().build());
        event.put(ModEntities.IM_CREEPER.get(),       IMCreeperEntity.createAttributes().build());
        event.put(ModEntities.IM_ZOMBIE_PIGMAN.get(), IMZombiePigmanEntity.createAttributes().build());
        event.put(ModEntities.IM_PIGMAN_ENGINEER.get(),      IMPigEngyEntity.createAttributes().build());
        event.put(ModEntities.IM_THROWER.get(),       IMThrowerEntity.createAttributes().build());
        event.put(ModEntities.IM_BURROWER.get(),      IMBurrowerEntity.createAttributes().build());
        event.put(ModEntities.IM_IMP.get(),           IMImpEntity.createAttributes().build());
        event.put(ModEntities.IM_WOLF.get(),          IMWolfEntity.createAttributes().build());
        event.put(ModEntities.IM_BIRD.get(),          IMBirdEntity.createAttributes().build());
        event.put(ModEntities.IM_GIANT_BIRD.get(),    IMGiantBirdEntity.createAttributes().build());
        event.put(ModEntities.IM_EGG.get(),           IMEggEntity.createAttributes().build());
        }
    }

    @EventBusSubscriber(modid = InvasionMod.MODID)
    public static final class GameBus {
        @SubscribeEvent
        public static void onRegisterCommands(RegisterCommandsEvent event) {
            //InvasionCommand.register(event.getDispatcher());
        }
    }
}
