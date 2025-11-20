package com.a3ot.disastermod.events.client;

import com.a3ot.disastermod.events.AbstractEvent;
import com.a3ot.disastermod.events.EventSide;
import com.a3ot.disastermod.events.EventType;
import net.minecraft.ChatFormatting;
import net.minecraft.world.level.Level;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public class FullBrightnessEvent implements AbstractEvent {
    private static Double originalBrightness = null;

    @Override
    @OnlyIn(Dist.CLIENT)
    public void onClientTick(Level level) { //todo придумать как вызывать метод в onClientStart и не давать игроку менять значение
        Minecraft mc = Minecraft.getInstance();
        if (originalBrightness == null) originalBrightness = mc.options.gamma().get();
        mc.options.gamma().set(12000D);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void onClientEnd(Level level) {
        Minecraft mc = Minecraft.getInstance();
        if (originalBrightness != null){
            mc.options.gamma().set(originalBrightness);
            originalBrightness = null;
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
        return ChatFormatting.YELLOW;
    }

    @Override
    public EventType getType() {
        return EventType.POSITIVE;
    }
}
