package com.a3ot.disastermod.handlers;

import com.a3ot.disastermod.ServerTick;
import com.a3ot.disastermod.data.DeathStorage;
import com.a3ot.disastermod.data.WorldData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.neoforged.neoforge.event.level.LevelEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;


@EventBusSubscriber
public class WorldDataHandler {

    private static WorldData overworldData = null;

    @SubscribeEvent
    public static void onWorldLoad(LevelEvent.Load event) {
        LevelAccessor levelAccessor = event.getLevel();
        if (levelAccessor instanceof ServerLevel serverLevel && serverLevel.dimension() == Level.OVERWORLD) {
            overworldData = get(serverLevel);
            ServerTick.setTickCounter(overworldData.getTickCounter());
            ServerTick.getActiveEvents().clear();
            ServerTick.getActiveEvents().putAll(overworldData.getActiveEvents());
            DeathStorage.setWorldDataDeathMap(overworldData.getDeathDataMap());
        }
    }

    @SubscribeEvent
    public static void onWorldUnload(LevelEvent.Unload event) {
        if (overworldData != null) {
            overworldData.setTickCounter(ServerTick.getTickCounter());
            overworldData.getActiveEvents().clear();
            overworldData.getActiveEvents().putAll(ServerTick.getActiveEvents());
            overworldData.getDeathDataMap().clear();
            overworldData.getDeathDataMap().putAll(DeathStorage.getWorldDataDeathMap());
        }
    }

    public static WorldData get(Level level) {
        if (level instanceof ServerLevel serverLevel && serverLevel.dimension() == Level.OVERWORLD) {
            return serverLevel.getDataStorage().computeIfAbsent(WorldData.FACTORY, WorldData.DATA_NAME);
        }
        throw new IllegalStateException("WorldData can only be accessed on the server side for Overworld");
    }
}
