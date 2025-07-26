package com.a3ot.disastermod.events.server;

import com.a3ot.disastermod.Disastermod;
import com.a3ot.disastermod.events.subclasses.AbstractPlayerAttributeEvent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

public class DeadlyFallsEvent extends AbstractPlayerAttributeEvent {
    private static final ResourceLocation FALL_DAMAGE = ResourceLocation.fromNamespaceAndPath(Disastermod.MODID, "deadly_falls");

    @Override
    protected void defineModifiers() {
        modifiers.put(Attributes.FALL_DAMAGE_MULTIPLIER, new AttributeModifier(FALL_DAMAGE, 100000, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
    }
}
