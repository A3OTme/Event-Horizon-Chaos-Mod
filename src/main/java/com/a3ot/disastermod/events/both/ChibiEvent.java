package com.a3ot.disastermod.events.both;

import com.a3ot.disastermod.Disastermod;
import com.a3ot.disastermod.events.EventSide;
import com.a3ot.disastermod.events.EventType;
import com.a3ot.disastermod.events.subclasses.AbstractPlayerAttributeEvent;
import com.a3ot.disastermod.handlers.client.ClientVariables;
import com.a3ot.disastermod.mixin.accessor.HumanoidModelAccessor;
import net.minecraft.ChatFormatting;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;

public class ChibiEvent extends AbstractPlayerAttributeEvent {
    private static final ResourceLocation CHIBI_ID = ResourceLocation.fromNamespaceAndPath(Disastermod.MODID, "chibi");

    @Override
    protected void defineModifiers() {
        modifiers.put(Attributes.SCALE, new AttributeModifier(CHIBI_ID, -0.6, AttributeModifier.Operation.ADD_VALUE));
    }

    @Override
    public void onClientStart(Level level) {
        ClientVariables.chibi = true;
    }

    @Override
    public void onClientEnd(Level level) {
        ClientVariables.chibi = false;
    }

    @Override
    public EventSide getSide() {
        return EventSide.BOTH;
    }

    public static void setupAnim(HumanoidModelAccessor accessor){
        ModelPart head = accessor.getHead();
        if (ClientVariables.chibi) head.xScale = head.yScale = head.zScale = 2.0F;
        else head.xScale = head.yScale = head.zScale = 1.0F;
    }

    @Override
    public EventType getType() {
        return EventType.POSITIVE;
    }

    @Override
    public ChatFormatting getColor() {
        return ChatFormatting.YELLOW;
    }
}
