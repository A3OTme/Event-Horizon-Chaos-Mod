package com.a3ot.eventhorizon.events.server;

import com.a3ot.eventhorizon.events.AbstractEvent;
import com.a3ot.eventhorizon.events.utils.Utils;
import com.a3ot.eventhorizon.registry.CursedVault;
import net.minecraft.ChatFormatting;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;


public class CursedVaultEvent implements AbstractEvent {
    @Override
    public void onStart(ServerLevel level) {
        level.players().stream().filter(Utils::isValidPlayer).forEach(player -> Utils.giveItem(player, CursedVault.createForGameplay(level)));
    }

    @Override
    public ChatFormatting getColor() {
        return ChatFormatting.DARK_PURPLE;
    }

    @Override
    public SoundEvent getSound() {
        return SoundEvents.VAULT_BREAK;
    }

    @Override
    public double getDefaultDurationMultiplier() {
        return 0;
    }
}