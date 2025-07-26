package com.a3ot.disastermod.events.client;

import com.a3ot.disastermod.events.AbstractEvent;
import com.a3ot.disastermod.events.EventSide;
import com.a3ot.disastermod.events.EventType;
import com.a3ot.disastermod.handlers.client.ClientVariables;
import net.minecraft.ChatFormatting;
import net.minecraft.world.level.Level;

public class OldPlayerAnimationEvent implements AbstractEvent {

    @Override
    public void onClientStart(Level level) {
        ClientVariables.oldPlayerAnimation = true;
    }

    @Override
    public void onClientEnd(Level level) {
        ClientVariables.oldPlayerAnimation = false;
    }

    @Override
    public EventSide getSide() {
        return EventSide.CLIENT;
    }

    @Override
    public double getDefaultDurationMultiplier() {
        return 4;
    }

    @Override
    public EventType getType() {
        return EventType.NEUTRAL;
    }

    @Override
    public ChatFormatting getColor() {
        return ChatFormatting.BLUE;
    }
}
