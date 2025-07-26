package com.a3ot.disastermod.events.server;

import com.a3ot.disastermod.Disastermod;
import com.a3ot.disastermod.events.EventType;
import com.a3ot.disastermod.events.subclasses.AbstractPlayerAttributeEvent;
import net.minecraft.ChatFormatting;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.neoforged.neoforge.common.NeoForgeMod;

public class CreativeFlyEvent extends AbstractPlayerAttributeEvent {
    private static final ResourceLocation CREATIVE_FLY = ResourceLocation.fromNamespaceAndPath(Disastermod.MODID, "creative_fly");

    @Override
    protected void defineModifiers() {
        modifiers.put(NeoForgeMod.CREATIVE_FLIGHT, new AttributeModifier(CREATIVE_FLY, 1, AttributeModifier.Operation.ADD_VALUE));
    }

    @Override
    public EventType getType() {
        return EventType.POSITIVE;
    }

    @Override
    public double getDefaultDurationMultiplier() {
        return 1;
    }

    @Override
    public ChatFormatting getColor() {
        return ChatFormatting.YELLOW;
    }

    @Override
    public SoundEvent getSound() {
        return SoundEvents.BEACON_POWER_SELECT;
    }
}
