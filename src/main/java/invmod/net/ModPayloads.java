package invmod.net;

import invmod.InvasionMod;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@EventBusSubscriber(modid = InvasionMod.MODID)
public final class ModPayloads {
    private ModPayloads() {}

    @SubscribeEvent
    public static void registerPayloads(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar reg = event.registrar("1");
        reg.playToServer(NexusActionPayload.TYPE, NexusActionPayload.CODEC, NexusActionPayload::handle);
    }
}
