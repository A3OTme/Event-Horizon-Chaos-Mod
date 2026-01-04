package com.a3ot.eventhorizon.events.client;

import com.a3ot.eventhorizon.events.AbstractEvent;
import com.a3ot.eventhorizon.events.EventSide;
import com.a3ot.eventhorizon.handlers.client.ClientVariables;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.world.level.Level;

public class NoTransparencyEvent implements AbstractEvent {

    @Override
    public void onClientStart(Level level) {
        ClientVariables.noTransparency = true;
        Minecraft mc = Minecraft.getInstance();
        mc.levelRenderer.allChanged();
    }

    @Override
    public void onClientEnd(Level level) {
        ClientVariables.noTransparency = false;
        Minecraft mc = Minecraft.getInstance();
        mc.levelRenderer.allChanged();
    }

    @Override
    public EventSide getSide() {
        return EventSide.CLIENT;
    }

    @Override
    public ChatFormatting getColor() {
        return ChatFormatting.DARK_AQUA;
    }
}
