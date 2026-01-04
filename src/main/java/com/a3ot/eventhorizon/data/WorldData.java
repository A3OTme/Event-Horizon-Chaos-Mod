package com.a3ot.eventhorizon.data;

import com.a3ot.eventhorizon.EventHorizon;
import com.a3ot.eventhorizon.events.AbstractEvent;
import com.a3ot.eventhorizon.events.EventsRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class WorldData extends SavedData {
    public static final String DATA_NAME = "eventhorizon_world_data";
    private int tickCounter = 0;
    private final Map<AbstractEvent, Integer> activeEvents = new HashMap<>();
    private final Map<UUID, DeathStorage.DeathData> deathDataMap = new HashMap<>();

    public static final Factory<WorldData> FACTORY = new Factory<>(
            WorldData::new,
            WorldData::load
    );

    @Override
    public @NotNull CompoundTag save(CompoundTag tag, HolderLookup.@NotNull Provider registries) {
        tag.putInt("tickCounter", tickCounter);
        EventHorizon.LOGGER.info("Saving tickCounter: {}", tickCounter);

        ListTag activeEventsList = new ListTag();
        for (Map.Entry<AbstractEvent, Integer> entry : activeEvents.entrySet()) {
            CompoundTag eventTag = new CompoundTag();
            eventTag.putString("event", entry.getKey().getConfigName());
            eventTag.putInt("duration", entry.getValue());
            activeEventsList.add(eventTag);
            EventHorizon.LOGGER.info("Saving event: {} duration: {}", entry.getKey().getConfigName(), entry.getValue());
        }
        tag.put("activeEvents", activeEventsList);
        CompoundTag deathDataTag = new CompoundTag();
        for (Map.Entry<UUID, DeathStorage.DeathData> entry : deathDataMap.entrySet()) {
            CompoundTag dataTag = new CompoundTag();
            dataTag.putInt("x", entry.getValue().pos.getX());
            dataTag.putInt("y", entry.getValue().pos.getY());
            dataTag.putInt("z", entry.getValue().pos.getZ());
            dataTag.putString("dimension", entry.getValue().dimension.location().toString());
            deathDataTag.put(entry.getKey().toString(), dataTag);
            EventHorizon.LOGGER.info("Saving deathPos: {}", entry.getKey());
        }
        tag.put("deathData", deathDataTag);
        return tag;
    }

    public static WorldData load(CompoundTag tag, HolderLookup.Provider registries) {
        WorldData data = new WorldData();
        data.tickCounter = tag.getInt("tickCounter");

        ListTag activeEventsList = tag.getList("activeEvents", Tag.TAG_COMPOUND);
        for (int i = 0; i < activeEventsList.size(); i++) {
            CompoundTag eventTag = activeEventsList.getCompound(i);
            String eventName = eventTag.getString("event");
            int duration = eventTag.getInt("duration");
            AbstractEvent event = EventsRegistry.getDisasterEventByName(eventName);
            if (event != null) data.activeEvents.put(event, duration);
        }

        CompoundTag deathDataTag = tag.getCompound("deathData");
        for (String uuidStr : deathDataTag.getAllKeys()) {
            try {
                UUID uuid = UUID.fromString(uuidStr);
                CompoundTag dataTag = deathDataTag.getCompound(uuidStr);
                BlockPos pos = new BlockPos(
                        dataTag.getInt("x"),
                        dataTag.getInt("y"),
                        dataTag.getInt("z")
                );
                ResourceKey<Level> dimension = ResourceKey.create(
                        Registries.DIMENSION,
                        ResourceLocation.parse(dataTag.getString("dimension"))
                );
                data.deathDataMap.put(uuid, new DeathStorage.DeathData(pos, dimension));
            } catch (Exception e) {
                EventHorizon.LOGGER.error("Failed to load death data for UUID: {}", uuidStr, e);
            }
        }
        return data;
    }

    public int getTickCounter() {
        return tickCounter;
    }

    public void setTickCounter(int newTickCounter) {
        this.tickCounter = newTickCounter;
        setDirty();
    }

    public Map<AbstractEvent, Integer> getActiveEvents() {
        return activeEvents;
    }

    public Map<UUID, DeathStorage.DeathData> getDeathDataMap() {
        return deathDataMap;
    }
}
