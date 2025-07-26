package com.a3ot.disastermod.handlers;

import com.a3ot.disastermod.Disastermod;
import com.a3ot.disastermod.commands.DisasterModCommands;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

@EventBusSubscriber(modid = Disastermod.MODID)
public class CommandEventHandler {

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        DisasterModCommands.register(event.getDispatcher());
    }
}