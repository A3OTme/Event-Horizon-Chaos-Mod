package com.a3ot.eventhorizon.events.server;

import com.a3ot.eventhorizon.EventHorizon;
import com.a3ot.eventhorizon.events.AbstractEvent;
import com.a3ot.eventhorizon.events.subclasses.AbstractAttributeEvent;
import com.a3ot.eventhorizon.events.utils.Utils;
import net.minecraft.ChatFormatting;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

public class NoGravityEvent extends AbstractAttributeEvent {
    private static final ResourceLocation GRAVITY_ID = ResourceLocation.fromNamespaceAndPath(EventHorizon.MOD_ID, "no_gravity");

    @Override
    protected void defineModifiers() {
        modifiers.put(Attributes.GRAVITY, new AttributeModifier(GRAVITY_ID, -0.08, AttributeModifier.Operation.ADD_VALUE));
    }

    @Override
    public void onStart(ServerLevel level) {
        level.players().stream().filter(Utils::isValidPlayer).forEach(player -> {
            player.setDeltaMovement(0, 1, 0);
            player.hurtMarked = true;
            player.resetFallDistance();
        });
    }

    @Override
    public boolean conflictsWith(AbstractEvent other) {
        return other instanceof LowGravityEvent;
    }

    @Override
    public ChatFormatting getColor() {
        return ChatFormatting.GRAY;
    }
}
