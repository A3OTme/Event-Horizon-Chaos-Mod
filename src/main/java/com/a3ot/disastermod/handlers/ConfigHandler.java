package com.a3ot.disastermod.handlers;

import com.a3ot.disastermod.config.GeneralConfig;
import com.a3ot.disastermod.Disastermod;
import com.a3ot.disastermod.ServerTick;
import com.a3ot.disastermod.events.EventsRegistry;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.config.ModConfigEvent;

@EventBusSubscriber(modid = Disastermod.MODID)
public class ConfigHandler {

    @SubscribeEvent
    public static void onConfigReload(ModConfigEvent.Reloading event) {
        if (event.getConfig().getModId().equals(Disastermod.MODID)) {
            EventsRegistry.updateEnabledEvents(GeneralConfig.EDIT_EVENTS);
            ServerTick.updateTotalTicks();
        }
    }
}