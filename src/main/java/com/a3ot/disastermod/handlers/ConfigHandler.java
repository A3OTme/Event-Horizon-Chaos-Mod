package com.a3ot.disastermod.handlers;

import com.a3ot.disastermod.config.ClientConfig;
import com.a3ot.disastermod.config.GeneralConfig;
import com.a3ot.disastermod.Disastermod;
import com.a3ot.disastermod.ServerTick;
import com.a3ot.disastermod.events.EventsRegistry;
import com.a3ot.disastermod.network.NetworkHandler;
import com.a3ot.disastermod.network.packet.SoundConfigPacket;
import net.minecraft.client.Minecraft;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.config.ModConfigEvent;

@EventBusSubscriber(modid = Disastermod.MODID)
public class ConfigHandler {

    @SubscribeEvent
    public static void onConfigReload(ModConfigEvent.Reloading event) {
        if (!event.getConfig().getModId().equals(Disastermod.MODID)) {
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