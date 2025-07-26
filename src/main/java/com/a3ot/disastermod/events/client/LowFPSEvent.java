package com.a3ot.disastermod.events.client;

import com.a3ot.disastermod.events.AbstractEvent;
import com.a3ot.disastermod.events.EventSide;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;


public class LowFPSEvent implements AbstractEvent {
    private static Integer originalFPS = null;

    @OnlyIn(Dist.CLIENT)
    public void onStart(Level level) {
        Minecraft mc = Minecraft.getInstance();
        originalFPS = mc.options.framerateLimit().get();
    }

    @OnlyIn(Dist.CLIENT)
    public void onClientTick(Level level) {
        Minecraft mc = Minecraft.getInstance();
        if (originalFPS == null) originalFPS = mc.options.framerateLimit().get();
        mc.options.framerateLimit().set(10);
    }

    @OnlyIn(Dist.CLIENT)
    public void onClientEnd(Level level) {
        Minecraft mc = Minecraft.getInstance();
        if (originalFPS != null){
            mc.options.framerateLimit().set(originalFPS);
            originalFPS = null;
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
}
