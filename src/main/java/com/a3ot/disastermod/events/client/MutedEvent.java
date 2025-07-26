package com.a3ot.disastermod.events.client;

import com.a3ot.disastermod.events.AbstractEvent;
import com.a3ot.disastermod.events.EventSide;
import com.a3ot.disastermod.handlers.client.ClientVariables;
import net.minecraft.ChatFormatting;
import net.minecraft.world.level.Level;

public class MutedEvent implements AbstractEvent {

    @Override
    public void onClientStart(Level level) {
        ClientVariables.muted = true;
    }

    @Override
    public void onClientEnd(Level level) {
        ClientVariables.muted = false;
    }

    @Override
    public EventSide getSide() {
        return EventSide.CLIENT;
    }

    @Override
    public ChatFormatting getColor() {
        return ChatFormatting.DARK_GRAY;
    }
}
