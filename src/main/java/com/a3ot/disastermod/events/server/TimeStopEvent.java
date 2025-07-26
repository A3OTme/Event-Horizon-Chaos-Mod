package com.a3ot.disastermod.events.server;

import com.a3ot.disastermod.ServerTick;
import com.a3ot.disastermod.events.AbstractEvent;
import com.a3ot.disastermod.events.EventType;
import net.minecraft.ChatFormatting;
import net.minecraft.server.level.ServerLevel;

public class TimeStopEvent implements AbstractEvent {

    @Override
    public void onStart(ServerLevel level) {
        level.getServer().tickRateManager().setFrozen(true);
    }

    @Override
    public void onEnd(ServerLevel level) {
        level.getServer().tickRateManager().setFrozen(false);
    }

    @Override
    public int getInterval() {
        return ServerTick.getTotalTicks();
    }

    @Override
    public EventType getType() {
        return EventType.NEUTRAL;
    }

    @Override
    public ChatFormatting getColor() {
        return ChatFormatting.GOLD;
    }
}
