package com.a3ot.eventhorizon.events.server;

import com.a3ot.eventhorizon.events.AbstractEvent;
import com.a3ot.eventhorizon.events.EventType;
import net.minecraft.ChatFormatting;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Mob;

public class BabyMobsEvent implements AbstractEvent {
    @Override
    public void onTick(ServerLevel level) {
        level.getAllEntities().forEach(entity -> {
            if (entity instanceof Mob livingEntity) {
                livingEntity.setBaby(true);
            }
        });
    }

    @Override
    public void onEnd(ServerLevel level) {
        level.getAllEntities().forEach(entity -> {
            if (entity instanceof Mob livingEntity) {
                livingEntity.setBaby(false);
            }
        });
    }

    @Override
    public boolean requiresPeriodicTick() {
        return true;
    }

    @Override
    public ChatFormatting getColor() {
        return ChatFormatting.LIGHT_PURPLE;
    }

    @Override
    public EventType getType() {
        return EventType.NEUTRAL;
    }
}
