package com.a3ot.disastermod.events.server;

import com.a3ot.disastermod.ServerTick;
import com.a3ot.disastermod.events.AbstractEvent;
import com.a3ot.disastermod.events.EventType;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.GameRules;

public class KeepInventoryEvent implements AbstractEvent {

    @Override
    public void onStart(ServerLevel level) {
        level.getServer().getGameRules().getRule(GameRules.RULE_KEEPINVENTORY).set(true, level.getServer());
    }

    @Override
    public void onEnd(ServerLevel level) {
        level.getServer().getGameRules().getRule(GameRules.RULE_KEEPINVENTORY).set(false, level.getServer());
    }

    @Override
    public EventType getType() {
        return EventType.POSITIVE;
    }

    @Override
    public int getInterval() {
        return ServerTick.getTotalTicks();
    }

}
