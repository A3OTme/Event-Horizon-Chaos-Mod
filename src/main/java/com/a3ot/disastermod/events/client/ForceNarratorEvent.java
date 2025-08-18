package com.a3ot.disastermod.events.client;

import com.a3ot.disastermod.events.AbstractEvent;
import com.a3ot.disastermod.events.EventSide;
import com.a3ot.disastermod.events.EventType;
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
    public void onClientTick(Level level) {
        Minecraft mc = Minecraft.getInstance();
        if (originalNarratorStatus == null) originalNarratorStatus = mc.options.narrator().get();
        mc.options.narrator().set(NarratorStatus.ALL);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void onClientEnd(Level level) {
        Minecraft mc = Minecraft.getInstance();
        if (originalNarratorStatus != null){
            mc.options.narrator().set(originalNarratorStatus);
            originalNarratorStatus = null;
        }
    }

    @Override
    public boolean requiresPeriodicClientTick() {
        return true;
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
        return 4;
    }

    @Override
    public EventType getType() {
        return EventType.NEUTRAL;
    }
}
