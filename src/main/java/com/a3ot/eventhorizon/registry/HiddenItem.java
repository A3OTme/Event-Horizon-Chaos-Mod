package com.a3ot.eventhorizon.registry;

import com.a3ot.eventhorizon.events.server.DementiaEvent;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.entity.player.Player;

public class HiddenItem extends Item {
    public HiddenItem(Properties properties) {
        super(properties);
    }

    @Override
    public boolean overrideStackedOnOther(ItemStack stack, Slot slot, ClickAction action, Player player) {
        if (action == ClickAction.PRIMARY || action == ClickAction.SECONDARY) {
            return handleItemReveal(stack, slot, player);
        }
        return false;
    }

    @Override
    public boolean overrideOtherStackedOnMe(ItemStack stack, ItemStack other, Slot slot, ClickAction action, Player player, SlotAccess access) {
        if (action == ClickAction.PRIMARY || action == ClickAction.SECONDARY) {
            return handleItemReveal(stack, slot, player);
        }
        return false;
    }

    private boolean handleItemReveal(ItemStack stack, Slot slot, Player player) {
        ItemStack result = DementiaEvent.removePlaceholder(stack);
        if (ItemStack.matches(stack, result)) return false;
        slot.set(result);
        player.getInventory().setChanged();
        return true;
    }
}