package com.a3ot.disastermod.events.server;

import com.a3ot.disastermod.events.AbstractEvent;
import com.a3ot.disastermod.events.EventType;
import com.a3ot.disastermod.events.subclasses.IActiveStateEvent;
import com.a3ot.disastermod.events.utils.Utils;
import net.minecraft.ChatFormatting;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.Items;

public class ArcheologyPlusEvent implements AbstractEvent, IActiveStateEvent {
    public static boolean active = false;

    @Override
    public void setActive() {
        active = true;
    }

    @Override
    public void setInactive() {
        active = false;
    }

    public static boolean isActive() {
        return active;
    }

    @Override
    public void onStart(ServerLevel level) {
        this.setActive();
        level.players().stream().filter(Utils::isPlayerValid).forEach(player -> Utils.giveItem(player, Items.BRUSH.getDefaultInstance()));
    }

    @Override
    public void onEnd(ServerLevel level) {
        this.setInactive();
    }

    @Override
    public ChatFormatting getColor() {
        return ChatFormatting.YELLOW;
    }

    @Override
    public SoundEvent getSound() {
        return SoundEvents.SUSPICIOUS_GRAVEL_STEP;
    }

    @Override
    public EventType getType() {
        return EventType.POSITIVE;
    }
}
