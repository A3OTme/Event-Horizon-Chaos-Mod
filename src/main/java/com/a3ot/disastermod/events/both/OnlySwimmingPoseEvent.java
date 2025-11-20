package com.a3ot.disastermod.events.both;

import com.a3ot.disastermod.Disastermod;
import com.a3ot.disastermod.events.EventSide;
import com.a3ot.disastermod.events.subclasses.AbstractPlayerAttributeEvent;
import com.a3ot.disastermod.handlers.client.ClientVariables;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;

public class OnlySwimmingPoseEvent extends AbstractPlayerAttributeEvent {
    private static final ResourceLocation OSP_ID = ResourceLocation.fromNamespaceAndPath(Disastermod.MODID, "osp");

    @Override
    public void onClientStart(Level level) {
        ClientVariables.onlySwimmingActive = true;
    }

    @Override
    public void onClientEnd(Level level) {
        ClientVariables.onlySwimmingActive = false;
    }

    public EventSide getSide() {
        return EventSide.BOTH;
    }

    @Override
    protected void defineModifiers() {
        modifiers.put(Attributes.SNEAKING_SPEED, new AttributeModifier(OSP_ID, 0.25, AttributeModifier.Operation.ADD_VALUE));
    }
}