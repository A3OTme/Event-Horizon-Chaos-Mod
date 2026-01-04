package com.a3ot.eventhorizon;

import com.a3ot.eventhorizon.config.GeneralConfig;
import com.a3ot.eventhorizon.data.ModDataComponents;
import com.a3ot.eventhorizon.events.EventsRegistry;
import com.a3ot.eventhorizon.events.utils.ModCatVariants;
import com.a3ot.eventhorizon.handlers.*;
import com.a3ot.eventhorizon.network.NetworkHandler;
import com.a3ot.eventhorizon.registry.ModCreativeModeTabs;
import com.a3ot.eventhorizon.registry.ModItems;
import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;

@Mod(EventHorizon.MOD_ID)
public class EventHorizon {

    public static final String MOD_ID = "eventhorizon";
    public static final Logger LOGGER = LogUtils.getLogger();

    public EventHorizon(IEventBus modEventBus, ModContainer modContainer) {
        modEventBus.addListener(this::commonSetup);
        ModDataComponents.DATA_COMPONENT_TYPES.register(modEventBus);
        ModDataComponents.ATTACHMENT_TYPES.register(modEventBus);
        ModCatVariants.CAT_VARIANTS.register(modEventBus);
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
