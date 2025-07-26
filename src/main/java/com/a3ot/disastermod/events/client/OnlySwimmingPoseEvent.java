package com.a3ot.disastermod.events.client;

import com.a3ot.disastermod.events.AbstractEvent;
import com.a3ot.disastermod.events.EventSide;
import com.a3ot.disastermod.handlers.client.ClientVariables;
import net.minecraft.world.level.Level;

public class OnlySwimmingPoseEvent implements AbstractEvent {

    @Override
    public void onClientStart(Level level) {
        ClientVariables.onlySwimmingActive = true;
    }

    @Override
    public void onClientEnd(Level level) {
        ClientVariables.onlySwimmingActive = false;
    }

    public EventSide getSide() {
        return EventSide.CLIENT;
    }
}