package com.a3ot.eventhorizon.handlers;

import com.a3ot.eventhorizon.config.ClientConfig;
import com.a3ot.eventhorizon.config.GeneralConfig;
import com.a3ot.eventhorizon.EventHorizon;
import com.a3ot.eventhorizon.ServerTick;
import com.a3ot.eventhorizon.events.EventsRegistry;
import com.a3ot.eventhorizon.network.NetworkHandler;
import com.a3ot.eventhorizon.network.packet.SoundConfigPacket;
import net.minecraft.client.Minecraft;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.config.ModConfigEvent;

@EventBusSubscriber(modid = EventHorizon.MOD_ID)
public class ConfigHandler {

    @SubscribeEvent
    public static void onConfigReload(ModConfigEvent.Reloading event) {
        if (!event.getConfig().getModId().equals(EventHorizon.MOD_ID)) {
            return;
        }

        if (event.getConfig().getSpec() == GeneralConfig.SPEC) {
            EventsRegistry.updateEnabledEvents(GeneralConfig.EDIT_EVENTS);
            ServerTick.updateTotalTicks();
        }

        if (event.getConfig().getSpec() == ClientConfig.SPEC) {
            if (Minecraft.getInstance().getConnection() != null) {
                NetworkHandler.sendToServer(new SoundConfigPacket(
                        ClientConfig.CLIENT_EVENT_SOUND.get(),
                        ClientConfig.CLIENT_WARNING_SOUND.get()
                ));
            }
        }
    }
}