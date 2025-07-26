package com.a3ot.disastermod.events.subclasses;

import net.minecraft.server.level.ServerLevel;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public abstract class AbstractDataComponentEvent extends AbstractInventoryEvent implements AbstractInventoryEvent.IBasicEvent{
    private static final Map<AbstractDataComponentEvent, Boolean> dataEvents = new HashMap<>();

    public static void registerEvent(AbstractDataComponentEvent event) {
        dataEvents.put(event, false);
    }

    public static Map<AbstractDataComponentEvent, Boolean> getDataEvents() {
        return Collections.unmodifiableMap(dataEvents);
    }

    public static void updateEventStatus(AbstractDataComponentEvent event, boolean isActive) {
        dataEvents.put(event, isActive);
    }

    @Override
    public void onTick(ServerLevel level) {
        super.onTick(level);
        updateEventStatus(this, true);
    }

    @Override
    public void onEnd(ServerLevel level) {
        updateEventStatus(this, false);
        super.onEnd(level);
    }
}
