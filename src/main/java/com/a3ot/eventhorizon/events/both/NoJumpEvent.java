package com.a3ot.eventhorizon.events.both;

import com.a3ot.eventhorizon.EventHorizon;
import com.a3ot.eventhorizon.events.EventSide;
import com.a3ot.eventhorizon.events.subclasses.AbstractPlayerAttributeEvent;
import com.a3ot.eventhorizon.handlers.client.ClientVariables;
import net.minecraft.client.player.Input;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;

public class NoJumpEvent extends AbstractPlayerAttributeEvent {
    private static final double jump_strength = 0.41999998688697815;

    private static final ResourceLocation JUMP_STRENGTH = ResourceLocation.fromNamespaceAndPath(EventHorizon.MOD_ID, "no_jump");

    @Override
    protected void defineModifiers() {
        modifiers.put(Attributes.JUMP_STRENGTH, new AttributeModifier(JUMP_STRENGTH, -jump_strength, AttributeModifier.Operation.ADD_VALUE));
    }

    @Override
    public void onClientStart(Level level) {
        ClientVariables.noJump = true;
    }

    @Override
    public void onClientEnd(Level level) {
        ClientVariables.noJump = false;
    }

    @Override
    public EventSide getSide() {
        return EventSide.BOTH;
    }

    public static void movementInput(Input input){
        if (ClientVariables.noJump) input.jumping = false;
    }
}
