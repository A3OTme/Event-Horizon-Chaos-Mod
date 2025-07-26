package com.a3ot.disastermod.events.subclasses;

import com.a3ot.disastermod.events.utils.Utils;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class AbstractRandomSlotInventoryEvent extends AbstractInventoryEvent {
    protected abstract int getSlotsToProcessCount();

    protected boolean isSlotEligible(Player player, int slot, ItemStack stack) {
        return !stack.isEmpty();
    }

    @Override
    public void onStart(ServerLevel level) {
        level.players().stream().filter(Utils::isPlayerValid).forEach(player -> {
            processRandomSlots(level, player);
            player.getInventory().setChanged();
        });
    }

    @Override
    public void onTick(ServerLevel level) {
        level.players().stream().filter(Utils::isPlayerValid).forEach(player -> {
            processRandomSlots(level, player);
            player.getInventory().setChanged();
        });
    }

    protected void processRandomSlots(ServerLevel level, Player player) {
        List<Integer> eligibleSlots = new ArrayList<>();
        for (int i = 0; i < 41; i++) {
            ItemStack stack = player.getInventory().getItem(i);

            boolean process = false;
            if (i < 36 && includeMainInventory()) process = true;
            else if (i >= 36 && i < 40 && includeArmor()) process = true;
            else if (i == 40 && includeOffhand()) process = true;

            if (!process) continue;

            if (isSlotEligible(player, i, stack)) {
                eligibleSlots.add(i);
            }
        }
        Collections.shuffle(eligibleSlots);
        int slotsToProcess = Math.min(getSlotsToProcessCount(), eligibleSlots.size());

        for (int i = 0; i < slotsToProcess; i++) {
            int slotIndex = eligibleSlots.get(i);
            ItemStack stack = player.getInventory().getItem(slotIndex);

            if (this instanceof ILevelAwareEvent levelEvent) {
                ItemStack newStack = levelEvent.modifyItem(stack, level);
                if (!ItemStack.matches(stack, newStack)) {
                    player.getInventory().setItem(slotIndex, newStack);
                }
            } else if (this instanceof IBasicEvent basicEvent) {
                ItemStack newStack = basicEvent.modifyItem(stack);
                if (!ItemStack.matches(stack, newStack)) {
                    player.getInventory().setItem(slotIndex, newStack);
                }
            }
        }
    }
}
