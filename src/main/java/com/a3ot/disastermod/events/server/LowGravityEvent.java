package com.a3ot.disastermod.events.server;

import com.a3ot.disastermod.Disastermod;
import com.a3ot.disastermod.events.AbstractEvent;
import com.a3ot.disastermod.events.EventType;
import com.a3ot.disastermod.events.subclasses.AbstractAttributeEvent;
import net.minecraft.ChatFormatting;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

public class LowGravityEvent extends AbstractAttributeEvent {
    private static final ResourceLocation LG_ID = ResourceLocation.fromNamespaceAndPath(Disastermod.MODID, "low_gravity");

    @Override
    protected void defineModifiers() {
        modifiers.put(Attributes.GRAVITY, new AttributeModifier(LG_ID, -0.8, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
        modifiers.put(Attributes.SAFE_FALL_DISTANCE, new AttributeModifier(LG_ID, 30, AttributeModifier.Operation.ADD_VALUE));
        modifiers.put(Attributes.FALL_DAMAGE_MULTIPLIER, new AttributeModifier(LG_ID, -0.2, AttributeModifier.Operation.ADD_MULTIPLIED_BASE));
        modifiers.put(Attributes.ATTACK_KNOCKBACK, new AttributeModifier(LG_ID, 1.7, AttributeModifier.Operation.ADD_VALUE));
        modifiers.put(Attributes.JUMP_STRENGTH, new AttributeModifier(LG_ID, -0.35, AttributeModifier.Operation.ADD_MULTIPLIED_BASE));
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
