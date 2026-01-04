package com.a3ot.eventhorizon.events.client;

import com.a3ot.eventhorizon.events.AbstractEvent;
import com.a3ot.eventhorizon.events.EventSide;
import com.a3ot.eventhorizon.events.EventType;
import com.a3ot.eventhorizon.handlers.client.ClientVariables;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.NarratorStatus;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public class ForceNarratorEvent implements AbstractEvent {
    private static NarratorStatus originalNarratorStatus = null;

    @Override
    @OnlyIn(Dist.CLIENT)
    public void onClientStart(Level level) {
        ClientVariables.forceNarrator = true;
        Minecraft mc = Minecraft.getInstance();
        if (originalNarratorStatus == null) originalNarratorStatus = mc.options.narrator().get();
        mc.options.narrator().set(NarratorStatus.ALL);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void onClientEnd(Level level) {
        ClientVariables.forceNarrator = false;
        Minecraft mc = Minecraft.getInstance();
        if (originalNarratorStatus != null){
            mc.options.narrator().set(originalNarratorStatus);
            originalNarratorStatus = null;
        }
    }

    @Override
    public EventSide getSide() {
        return EventSide.CLIENT;
    }

    @Override
    public ChatFormatting getColor() {
        return ChatFormatting.BLUE;
    }

    @Override
    public double getDefaultDurationMultiplier() {
        return 3;
    }

    @Override
    public EventType getType() {
        return EventType.NEGATIVE;
    }
}
