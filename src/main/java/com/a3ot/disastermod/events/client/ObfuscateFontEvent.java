package com.a3ot.disastermod.events.client;

import com.a3ot.disastermod.events.AbstractEvent;
import com.a3ot.disastermod.events.EventSide;
import com.a3ot.disastermod.handlers.client.ClientVariables;
import net.minecraft.ChatFormatting;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.Level;

public class ObfuscateFontEvent implements AbstractEvent {

    @Override
    public void onClientStart(Level level) {
        ClientVariables.obfuscate = true;
    }

    @Override
    public void onClientEnd(Level level) {
        ClientVariables.obfuscate = false;
    }

    @Override
    public EventSide getSide() {
        return EventSide.CLIENT;
    }

    @Override
    public float getPitch() {
        return 0.4F;
    }

    @Override
    public ChatFormatting getColor() {
        return ChatFormatting.DARK_PURPLE;
    }

    @Override
    public SoundEvent getSound() {
        return SoundEvents.ENCHANTMENT_TABLE_USE;
    }
}
