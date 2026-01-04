package com.a3ot.eventhorizon.events.client;

import com.a3ot.eventhorizon.events.AbstractEvent;
import com.a3ot.eventhorizon.events.EventSide;
import com.a3ot.eventhorizon.handlers.client.ClientVariables;
import net.minecraft.ChatFormatting;
import net.minecraft.world.level.Level;

public class RandomSoundsEvent implements AbstractEvent {

    @Override
    public void onClientStart(Level level) {
        ClientVariables.randomSounds = true;
    }

    @Override
    public void onClientEnd(Level level) {
        ClientVariables.randomSounds = false;
    }

    @Override
    public EventSide getSide() {
        return EventSide.CLIENT;
    }

    @Override
    public ChatFormatting getColor() {
        return ChatFormatting.BLUE;
    }

    @Override
    public boolean conflictsWith(AbstractEvent other) {
        return other instanceof MutedEvent;
    }
}
