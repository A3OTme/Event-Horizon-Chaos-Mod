package com.a3ot.eventhorizon.events.client;

import com.a3ot.eventhorizon.events.AbstractEvent;
import com.a3ot.eventhorizon.events.EventSide;
import com.a3ot.eventhorizon.handlers.client.ClientVariables;
import net.minecraft.ChatFormatting;
import net.minecraft.client.player.Input;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.Level;

public class InvertedControlEvent implements AbstractEvent {

    @Override
    public void onClientStart(Level level) {
        ClientVariables.invertedControl = true;
    }

    @Override
    public void onClientEnd(Level level) {
        ClientVariables.invertedControl = false;
    }

    @Override
    public EventSide getSide() {
        return EventSide.CLIENT;
    }

    public static void movementInput(Input input){
        if (!ClientVariables.invertedControl) return;
        input.forwardImpulse = -input.forwardImpulse;
        input.leftImpulse = -input.leftImpulse;
        boolean originalJumping = input.jumping;
        input.jumping = input.shiftKeyDown;
        input.shiftKeyDown = originalJumping;
    }

    @Override
    public ChatFormatting getColor() {
        return ChatFormatting.DARK_AQUA;
    }

    @Override
    public float getPitch() {
        return 1.5F;
    }

    @Override
    public SoundEvent getSound() {
        return SoundEvents.ILLUSIONER_PREPARE_MIRROR;
    }
}
