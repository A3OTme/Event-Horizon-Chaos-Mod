package com.a3ot.eventhorizon.events.client;

import com.a3ot.eventhorizon.events.AbstractEvent;
import com.a3ot.eventhorizon.events.EventSide;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public class SmallGUIEvent implements AbstractEvent {
    private static Integer originalGUIScale = null;

    @Override
    @OnlyIn(Dist.CLIENT)
    public void onClientTick(Level level) {
        Minecraft mc = Minecraft.getInstance();
        if (originalGUIScale == null) originalGUIScale = mc.options.guiScale().get();
        mc.options.guiScale().set(1);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void onClientEnd(Level level) {
        Minecraft mc = Minecraft.getInstance();
        if (originalGUIScale != null){
            mc.options.guiScale().set(originalGUIScale);
            originalGUIScale = null;
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
        return ChatFormatting.DARK_PURPLE;
    }
}