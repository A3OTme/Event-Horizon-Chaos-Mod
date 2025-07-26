package com.a3ot.disastermod.handlers;

import com.a3ot.disastermod.events.server.DementiaEvent;
import com.a3ot.disastermod.events.subclasses.AbstractDataComponentEvent;
import net.minecraft.core.NonNullList;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.ItemEntityPickupEvent;
import net.neoforged.neoforge.event.entity.player.PlayerContainerEvent;

@EventBusSubscriber
public class DataHandler {
    private static void processItemStack(ItemStack stack) {
        if (stack.isEmpty()) return;
        AbstractDataComponentEvent.getDataEvents().forEach((event, isActive) -> {
            if (isActive) event.modifyItem(stack);
            else event.restoreItem(stack);
        });
    }

    @SubscribeEvent
    public static void onItemPickup(ItemEntityPickupEvent.Pre event) {
        DementiaEvent.itemPickup(event);
        processItemStack(event.getItemEntity().getItem());
    }

    @SubscribeEvent
    public static void onContainerOpen(PlayerContainerEvent event) {
        AbstractContainerMenu container = event.getContainer();
        Player player = event.getEntity();
        container.slots.forEach(slot -> processItemStack(slot.getItem()));
        container.broadcastChanges();
        player.getInventory().setChanged();
    }
}
