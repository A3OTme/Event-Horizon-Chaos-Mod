package com.a3ot.eventhorizon;

import com.a3ot.eventhorizon.config.ClientConfig;
import com.a3ot.eventhorizon.handlers.client.ClientHandler;
import com.a3ot.eventhorizon.handlers.client.ClientVariables;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.common.NeoForge;

@Mod(value = EventHorizon.MOD_ID, dist = Dist.CLIENT)
public class EventHorizonClient {
    public EventHorizonClient(ModContainer container) {
        container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
        NeoForge.EVENT_BUS.register(ClientHandler.class);
        NeoForge.EVENT_BUS.register(ClientVariables.class);
        container.registerConfig(ModConfig.Type.CLIENT, ClientConfig.SPEC);
    }
}