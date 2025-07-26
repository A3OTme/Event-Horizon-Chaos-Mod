package com.a3ot.disastermod.events.server;

import com.a3ot.disastermod.Disastermod;
import com.a3ot.disastermod.events.AbstractEvent;
import com.a3ot.disastermod.events.EventType;
import com.a3ot.disastermod.events.subclasses.AbstractAttributeEvent;
import net.minecraft.ChatFormatting;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

public class MoonGravityEvent extends AbstractAttributeEvent {
    private static final ResourceLocation GRAVITY_ID = ResourceLocation.fromNamespaceAndPath(Disastermod.MODID, "moon_gravity");
    private static final ResourceLocation SFD_ID = ResourceLocation.fromNamespaceAndPath(Disastermod.MODID, "moon_sfd");


    @Override
    protected void defineModifiers() {
        modifiers.put(Attributes.GRAVITY, new AttributeModifier(GRAVITY_ID, -0.07, AttributeModifier.Operation.ADD_VALUE));
        modifiers.put(Attributes.SAFE_FALL_DISTANCE, new AttributeModifier(SFD_ID, 15, AttributeModifier.Operation.ADD_VALUE));
    }

    @Override
    public boolean conflictsWith(AbstractEvent other) {
        return other instanceof NoGravityEvent;
    }

    @Override
    public EventType getType() {
        return EventType.NEUTRAL;
    }

    @Override
    public ChatFormatting getColor() {
        return ChatFormatting.GRAY;
    }
}
