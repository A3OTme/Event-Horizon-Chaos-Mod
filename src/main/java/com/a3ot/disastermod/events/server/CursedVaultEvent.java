package com.a3ot.disastermod.events.server;

import com.a3ot.disastermod.events.AbstractEvent;
import com.a3ot.disastermod.events.utils.Utils;
import com.a3ot.disastermod.registry.CursedVault;
import net.minecraft.ChatFormatting;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;


public class CursedVaultEvent implements AbstractEvent {
    @Override
    public void onStart(ServerLevel level) {
        level.players().stream().filter(Utils::isPlayerValid).forEach(player -> Utils.giveItem(player, CursedVault.createForGameplay(level)));
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