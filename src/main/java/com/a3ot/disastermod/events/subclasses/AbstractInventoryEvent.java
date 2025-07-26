package com.a3ot.disastermod.events.subclasses;

import com.a3ot.disastermod.events.AbstractEvent;
import com.a3ot.disastermod.events.utils.Utils;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public abstract class AbstractInventoryEvent implements AbstractEvent {

    @Override
    public void onStart(ServerLevel level) {
        level.players().stream().filter(Utils::isPlayerValid).forEach(player -> {
            processAllSlots(level, player);
            player.getInventory().setChanged();
        });
    }

    @Override
    public void onTick(ServerLevel level) {
        level.players().stream().filter(Utils::isPlayerValid).forEach(player -> {
            processAllSlots(level, player);
            player.getInventory().setChanged();
        });
    }

    @Override
    public void onEnd(ServerLevel level) {
        level.players().forEach(player -> {
            restoreAllSlots(level, player);
            player.getInventory().setChanged();
        });
    }

    @Override
    public boolean requiresPeriodicTick() {
        return true;
    }

    protected boolean includeMainInventory() { return true; }
    protected boolean includeArmor() { return true; }
    protected boolean includeOffhand() { return true; }

    protected void processAllSlots(ServerLevel level, Player player) {
        for (int i = 0; i < 41; i++) {
            ItemStack stack = player.getInventory().getItem(i);
            if (stack.isEmpty()) continue;

            boolean process = false;
            if (i < 36 && includeMainInventory()) process = true;
            else if (i >= 36 && i < 40 && includeArmor()) process = true;
            else if (i == 40 && includeOffhand()) process = true;

            if (!process) continue;

            if (this instanceof ILevelAwareEvent levelEvent) {
                ItemStack newStack = levelEvent.modifyItem(stack, level);
                if (!ItemStack.matches(stack, newStack)) {
                    player.getInventory().setItem(i, newStack);
                }
            } else if (this instanceof IBasicEvent basicEvent) {
                ItemStack newStack = basicEvent.modifyItem(stack);
                if (!ItemStack.matches(stack, newStack)) {
                    player.getInventory().setItem(i, newStack);
                }
            }
        }
    }

    protected void restoreAllSlots(ServerLevel level, Player player) {
        for (int i = 0; i < 41; i++) {
            ItemStack stack = player.getInventory().getItem(i);
            if (stack.isEmpty()) continue;
            if (this instanceof ILevelAwareEvent levelEvent) {
                ItemStack newStack = levelEvent.restoreItem(stack, level);
                if (!ItemStack.matches(stack, newStack)) {
                    player.getInventory().setItem(i, newStack);
                }
            } else if (this instanceof IBasicEvent basicEvent) {
                ItemStack newStack = basicEvent.restoreItem(stack);
                if (!ItemStack.matches(stack, newStack)) {
                    player.getInventory().setItem(i, newStack);
                }
            }
        }
    }

    public interface ILevelAwareEvent {
        ItemStack modifyItem(ItemStack stack, ServerLevel level);
        default ItemStack restoreItem(ItemStack stack, ServerLevel level) {
            return stack;
        }
    }

    public interface IBasicEvent {
        ItemStack modifyItem(ItemStack stack);
        default ItemStack restoreItem(ItemStack stack) {
            return stack;
        }
    }
}
