package com.a3ot.eventhorizon.events.client;

import com.a3ot.eventhorizon.events.AbstractEvent;
import com.a3ot.eventhorizon.events.EventSide;
import com.a3ot.eventhorizon.handlers.client.ClientVariables;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.Level;

public class NoInventoryEvent implements AbstractEvent {

    @Override
    public void onClientStart(Level level) {
        ClientVariables.noInventory = true;
    }

    @Override
    public void onClientEnd(Level level) {
        ClientVariables.noInventory = false;
    }

    public static boolean handleKeybinds(){
        if(ClientVariables.noInventory){
            Minecraft mc = Minecraft.getInstance();
            if(mc.player != null){
                if (mc.options.keyInventory.consumeClick()) {
                    mc.player.sendSystemMessage(Component.translatable("eventhorizon.configuration.noInventory.locked").withStyle(ChatFormatting.RED));
                    return true;
                }
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
        return SoundEvents.CHEST_LOCKED;
    }
}
