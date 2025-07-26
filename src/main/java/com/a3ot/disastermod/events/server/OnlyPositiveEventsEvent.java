package com.a3ot.disastermod.events.server;

import com.a3ot.disastermod.events.AbstractEvent;
import com.a3ot.disastermod.events.EventType;
import com.a3ot.disastermod.events.subclasses.IActiveStateEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;

public class OnlyPositiveEventsEvent implements AbstractEvent, IActiveStateEvent {
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
    public boolean conflictsWith(AbstractEvent other) {
        return other instanceof OnlyNegativeEventsEvent;
    }

    @Override
    public SoundEvent getSound() {
        return SoundEvents.VILLAGER_YES;
    }

    @Override
    public EventType getType() {
        return EventType.POSITIVE;
    }
}
