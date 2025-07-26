package com.a3ot.disastermod.events.client;

import com.a3ot.disastermod.events.AbstractEvent;
import com.a3ot.disastermod.events.EventSide;
import com.a3ot.disastermod.events.EventType;
import com.a3ot.disastermod.handlers.client.ClientVariables;
import net.minecraft.world.level.Level;

public class PitchMinEventEvent implements AbstractEvent {

    @Override
    public void onClientStart(Level level) {
        ClientVariables.pitchMin = true;
    }

    @Override
    public void onClientEnd(Level level) {
        ClientVariables.pitchMin = false;
    }

    @Override
    public EventSide getSide() {
        return EventSide.CLIENT;
    }

    @Override
    public boolean conflictsWith(AbstractEvent other) {
        return other instanceof PitchMaxEventEvent;
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
