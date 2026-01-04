package com.a3ot.eventhorizon.events.client;

import com.a3ot.eventhorizon.events.AbstractEvent;
import com.a3ot.eventhorizon.events.EventSide;
import com.a3ot.eventhorizon.handlers.client.ClientVariables;
import net.minecraft.client.Minecraft;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.Level;

public class SmoothCameraEvent implements AbstractEvent {

    @Override
    public void onClientStart(Level level) {
        ClientVariables.smoothCamera = true;
        Minecraft mc = Minecraft.getInstance();
        mc.options.smoothCamera = true;
    }

    @Override
    public void onClientEnd(Level level) {
        ClientVariables.smoothCamera = false;
        Minecraft mc = Minecraft.getInstance();
        mc.options.smoothCamera = false;
    }

    public static boolean handleKeybinds(){
        if(ClientVariables.smoothCamera){
            Minecraft mc = Minecraft.getInstance();
            if(mc.player != null){
                mc.options.smoothCamera = true;
                return mc.options.keySmoothCamera.consumeClick();
            }
        }
        return false;
    }

    @Override
    public EventSide getSide() {
        return EventSide.CLIENT;
    }

    @Override
    public SoundEvent getSound() {
        return SoundEvents.SPYGLASS_USE;
    }
}
