package com.a3ot.disastermod.events.server;

import com.a3ot.disastermod.events.AbstractEvent;
import com.a3ot.disastermod.events.utils.Utils;
import net.minecraft.ChatFormatting;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.GameType;

public class AdventureModeEvent implements AbstractEvent {
    @Override
    public void onTick(ServerLevel level) {
        level.players().stream().filter(Utils::isValidPlayer).forEach(player -> {
            if (player.gameMode.isSurvival()) player.setGameMode(GameType.ADVENTURE);
        });
    }

    @Override
    public void onEnd(ServerLevel level) {
        level.players().forEach(player -> {
            if (player.gameMode.getGameModeForPlayer()==GameType.ADVENTURE) player.setGameMode(GameType.SURVIVAL);
        });
    }

    @Override
    public boolean requiresPeriodicTick() {
        return true;
    }

    @Override
    public ChatFormatting getColor() {
        return ChatFormatting.WHITE;
    }

    @Override
    public SoundEvent getSound() {
        return SoundEvents.ELDER_GUARDIAN_CURSE;
    }
}
