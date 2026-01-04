package com.a3ot.eventhorizon.handlers;

import com.a3ot.eventhorizon.EventHorizon;
import com.a3ot.eventhorizon.commands.EventHorizonCommands;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

@EventBusSubscriber(modid = EventHorizon.MOD_ID)
public class CommandEventHandler {

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        EventHorizonCommands.register(event.getDispatcher());
    }
}