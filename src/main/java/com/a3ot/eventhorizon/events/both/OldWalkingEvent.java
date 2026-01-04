package com.a3ot.eventhorizon.events.both;

import com.a3ot.eventhorizon.events.AbstractEvent;
import com.a3ot.eventhorizon.events.EventSide;
import com.a3ot.eventhorizon.handlers.client.ClientVariables;
import net.minecraft.ChatFormatting;
import net.minecraft.client.player.Input;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.level.Level;

public class OldWalkingEvent implements AbstractEvent {
    private static int swayCounter = 0;

    @Override
    public void onStart(ServerLevel level) {
        for (ServerPlayer player : level.players()) {
            MobEffectInstance slowFallingEffect = new MobEffectInstance(MobEffects.SLOW_FALLING, 30, 0, false, false);
            player.addEffect(slowFallingEffect);
        }
    }

    @Override
    public void onClientStart(Level level) {
        ClientVariables.oldWalking = true;
    }

    @Override
    public void onClientEnd(Level level) {
        ClientVariables.oldWalking = false;
    }

    public static void movementInput(Input input){
        if (!ClientVariables.oldWalking) return;
        swayCounter++;
        input.jumping = true;
        input.forwardImpulse = 1;
        if (swayCounter % 40 < 20){
            input.leftImpulse = 1;
        } else {
            input.leftImpulse = -1;
        }
    }

    @Override
    public EventSide getSide() {
        return EventSide.BOTH;
    }

    @Override
    public ChatFormatting getColor() {
        return ChatFormatting.BLUE;
    }
}
