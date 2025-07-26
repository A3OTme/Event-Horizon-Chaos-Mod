package com.a3ot.disastermod.events.client;

import com.a3ot.disastermod.events.AbstractEvent;
import com.a3ot.disastermod.events.EventSide;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public class LowRenderDistanceEvent implements AbstractEvent {
    private static Integer originalDistance = null;

    @OnlyIn(Dist.CLIENT)
    public void onStart(Level level) {
        Minecraft mc = Minecraft.getInstance();
        originalDistance = mc.options.renderDistance().get();
    }

    @OnlyIn(Dist.CLIENT)
    public void onClientTick(Level level) {
        Minecraft mc = Minecraft.getInstance();
        if (originalDistance == null) originalDistance = mc.options.renderDistance().get();
        mc.options.renderDistance().set(2);
    }

    @OnlyIn(Dist.CLIENT)
    public void onClientEnd(Level level) {
        Minecraft mc = Minecraft.getInstance();
        if (originalDistance != null){
            mc.options.renderDistance().set(originalDistance);
            originalDistance = null;
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
        return ChatFormatting.GRAY;
    }
}
