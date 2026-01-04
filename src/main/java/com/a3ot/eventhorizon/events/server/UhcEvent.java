package com.a3ot.eventhorizon.events.server;

import com.a3ot.eventhorizon.events.AbstractEvent;
import net.minecraft.ChatFormatting;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.GameRules;

public class UhcEvent implements AbstractEvent {

    @Override
    public void onStart(ServerLevel level) {
        level.getServer().getGameRules().getRule(GameRules.RULE_NATURAL_REGENERATION).set(false, level.getServer());
    }

    @Override
    public void onEnd(ServerLevel level) {
        level.getServer().getGameRules().getRule(GameRules.RULE_NATURAL_REGENERATION).set(true, level.getServer());
    }

    @Override
    public ChatFormatting getColor() {
        return ChatFormatting.DARK_RED;
    }
}
