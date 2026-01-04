package com.a3ot.eventhorizon.events.server;

import com.a3ot.eventhorizon.events.AbstractEvent;
import com.a3ot.eventhorizon.events.EventType;
import com.a3ot.eventhorizon.events.utils.IActiveStateEvent;
import net.minecraft.server.level.ServerLevel;

public class RandomBlockDropEvent implements AbstractEvent, IActiveStateEvent {
    private static boolean active = false;

    @Override
    public void setActive() {
        active = true;
    }

    @Override
    public void setInactive() {
        active = false;
    }

    public static boolean isActive() {
        return active;
    }

    @Override
    public void onStart(ServerLevel level) {
        this.setActive();
    }

    @Override
    public void onEnd(ServerLevel level) {
        this.setInactive();
    }

    @Override
    public EventType getType() {
        return EventType.POSITIVE;
    }

    @Override
    public double getDefaultDurationMultiplier() {
        return 1;
    }
}