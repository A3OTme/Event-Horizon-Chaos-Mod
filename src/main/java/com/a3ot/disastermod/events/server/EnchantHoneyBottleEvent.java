package com.a3ot.disastermod.events.server;

import com.a3ot.disastermod.events.AbstractEvent;
import com.a3ot.disastermod.events.EventType;
import com.a3ot.disastermod.events.subclasses.IActiveStateEvent;
import com.a3ot.disastermod.events.utils.Utils;
import com.a3ot.disastermod.registry.ModItems;
import net.minecraft.ChatFormatting;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.Level;

import java.util.List;

public class EnchantHoneyBottleEvent implements AbstractEvent, IActiveStateEvent {
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
        if (level.dimension() != Level.OVERWORLD) return;
        List<ServerPlayer> players = level.getServer().getPlayerList().getPlayers().stream().filter(Utils::isValidPlayer).toList();
        if (players.isEmpty()) return;
        Utils.giveItem(players.get(level.random.nextInt(players.size())), ModItems.ENCHANTED_HONEY_BOTTLE.toStack()); //нельзя вызывать так как повторно выдаст игрокам предмет.
    }

    @Override
    public void onEnd(ServerLevel level) {
        this.setInactive();
    }

    @Override
    public double getDefaultDurationMultiplier() {
        return 5;
    }

    @Override
    public ChatFormatting getColor() {
        return ChatFormatting.GOLD;
    }

    @Override
    public SoundEvent getSound() {
        return SoundEvents.BEEHIVE_ENTER;
    }

    @Override
    public EventType getType() {
        return EventType.POSITIVE;
    }
}