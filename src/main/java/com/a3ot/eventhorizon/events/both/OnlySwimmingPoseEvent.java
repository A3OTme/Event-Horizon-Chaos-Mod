package com.a3ot.eventhorizon.events.both;

import com.a3ot.eventhorizon.EventHorizon;
import com.a3ot.eventhorizon.events.EventSide;
import com.a3ot.eventhorizon.events.subclasses.AbstractPlayerAttributeEvent;
import com.a3ot.eventhorizon.handlers.client.ClientVariables;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;

public class OnlySwimmingPoseEvent extends AbstractPlayerAttributeEvent {
    private static final ResourceLocation OSP_ID = ResourceLocation.fromNamespaceAndPath(EventHorizon.MOD_ID, "osp");

    @Override
    public void onClientStart(Level level) {
        ClientVariables.onlySwimming = true;
    }

    @Override
    public void onClientEnd(Level level) {
        ClientVariables.onlySwimming = false;
    }

    public EventSide getSide() {
        return EventSide.BOTH;
    }

    @Override
    protected void defineModifiers() {
        modifiers.put(Attributes.SNEAKING_SPEED, new AttributeModifier(OSP_ID, 0.25, AttributeModifier.Operation.ADD_VALUE));
    }
}