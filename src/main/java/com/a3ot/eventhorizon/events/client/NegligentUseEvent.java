package com.a3ot.eventhorizon.events.client;

import com.a3ot.eventhorizon.events.AbstractEvent;
import com.a3ot.eventhorizon.events.EventSide;
import com.a3ot.eventhorizon.handlers.client.ClientVariables;
import net.minecraft.ChatFormatting;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.Level;

public class NegligentUseEvent implements AbstractEvent {
    @Override
    public void onClientStart(Level level) {
        ClientVariables.negligentUse = true;
    }

    @Override
    public void onClientEnd(Level level) {
        ClientVariables.negligentUse = false;
    }

    @Override
    public double getDefaultDurationMultiplier() {
        return 1.5;
    }

    @Override
    public ChatFormatting getColor() {
        return ChatFormatting.DARK_AQUA;
    }

    @Override
    public SoundEvent getSound() {
        return SoundEvents.ITEM_BREAK;
    }

    @Override
    public EventSide getSide() {
        return EventSide.CLIENT;
    }
}