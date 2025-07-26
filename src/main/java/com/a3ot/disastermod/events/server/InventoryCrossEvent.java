package com.a3ot.disastermod.events.server;

import com.a3ot.disastermod.events.AbstractEvent;
import com.a3ot.disastermod.events.utils.Utils;
import net.minecraft.ChatFormatting;
import net.minecraft.core.NonNullList;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class InventoryCrossEvent implements AbstractEvent {
    @Override
    public void onStart(ServerLevel level) {
        List<ServerPlayer> players = level.players().stream().filter(Utils::isPlayerValid).toList();

        if (players.size() < 2) return;

        List<NonNullList<ItemStack>> inventoryCopies = new ArrayList<>();
        for (ServerPlayer player : players) {
            NonNullList<ItemStack> copy = NonNullList.create();
            for (ItemStack stack : player.getInventory().items) {
                copy.add(stack.copy());
            }
            inventoryCopies.add(copy);
        }
        for (int i = 0; i < players.size(); i++) {
            ServerPlayer player = players.get(i);
            NonNullList<ItemStack> targetInventory = inventoryCopies.get((i + 1) % players.size());
            for (int slot = 0; slot < player.getInventory().items.size(); slot++) {
                player.getInventory().items.set(slot, targetInventory.get(slot).copy());
            }
            player.getInventory().setChanged();
        }
    }

    @Override
    public double getDefaultDurationMultiplier() {
        return 0;
    }

    @Override
    public ChatFormatting getColor() {
        return ChatFormatting.DARK_AQUA;
    }

    @Override
    public SoundEvent getSound() {
        return SoundEvents.ILLUSIONER_MIRROR_MOVE;
    }
}
