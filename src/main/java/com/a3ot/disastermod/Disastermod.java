package com.a3ot.disastermod;

import com.a3ot.disastermod.config.GeneralConfig;
import com.a3ot.disastermod.data.ModDataComponents;
import com.a3ot.disastermod.events.EventsRegistry;
import com.a3ot.disastermod.handlers.*;
import com.a3ot.disastermod.network.NetworkHandler;
import com.a3ot.disastermod.registry.ModCreativeModeTabs;
import com.a3ot.disastermod.registry.ModItems;
import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;

@Mod(Disastermod.MODID)
public class Disastermod {

    public static final String MODID = "disastermod";
    public static final Logger LOGGER = LogUtils.getLogger();

    public Disastermod(IEventBus modEventBus, ModContainer modContainer) {
        modEventBus.addListener(this::commonSetup);
        ModDataComponents.DATA_COMPONENT_TYPES.register(modEventBus);
        modEventBus.register(ConfigHandler.class);
        modEventBus.register(NetworkHandler.class);
        NeoForge.EVENT_BUS.register(BlockHandler.class);
        NeoForge.EVENT_BUS.register(PlayerHandler.class);
        NeoForge.EVENT_BUS.register(ServerHandler.class);
        NeoForge.EVENT_BUS.register(WorldDataHandler.class);
        NeoForge.EVENT_BUS.addListener(CommandEventHandler::onRegisterCommands);
        ModItems.ITEMS.register(modEventBus);
        ModCreativeModeTabs.CREATIVE_MODE_TABS.register(modEventBus);
        modContainer.registerConfig(ModConfig.Type.COMMON, GeneralConfig.SPEC);
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            EventsRegistry.updateEnabledEvents(GeneralConfig.EDIT_EVENTS);
            ServerTick.updateTotalTicks();
        });
    }


}
