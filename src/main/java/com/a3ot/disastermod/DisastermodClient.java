package com.a3ot.disastermod;

import com.a3ot.disastermod.config.ClientConfig;
import com.a3ot.disastermod.config.GeneralConfig;
import com.a3ot.disastermod.events.EventsRegistry;
import com.a3ot.disastermod.handlers.client.ClientHandler;
import com.a3ot.disastermod.handlers.client.ClientVariables;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.common.NeoForge;

@Mod(value = Disastermod.MODID, dist = Dist.CLIENT)
public class DisastermodClient {
    public DisastermodClient(ModContainer container) {
        container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
        NeoForge.EVENT_BUS.register(ClientHandler.class);
        NeoForge.EVENT_BUS.register(ClientVariables.class);
        container.registerConfig(ModConfig.Type.CLIENT, ClientConfig.SPEC);
    }
}