package com.a3ot.eventhorizon.events.client;

import com.a3ot.eventhorizon.events.AbstractEvent;
import com.a3ot.eventhorizon.events.EventSide;
import com.a3ot.eventhorizon.events.EventType;
import com.a3ot.eventhorizon.handlers.client.ClientVariables;
import net.minecraft.world.level.Level;

public class PitchMaxEvent implements AbstractEvent {

    @Override
    public void onClientStart(Level level) {
        ClientVariables.pitchMax = true;
    }

    @Override
    public void onClientEnd(Level level) {
        ClientVariables.pitchMax = false;
    }

    @Override
    public EventSide getSide() {
        return EventSide.CLIENT;
    }

    @Override
    public boolean conflictsWith(AbstractEvent other) {
        return other instanceof PitchMinEvent;
    }

    @Override
    public double getDefaultDurationMultiplier() {
        return 4;
    }

    @Override
    public EventType getType() {
        return EventType.NEUTRAL;
    }
}
