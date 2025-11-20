package com.a3ot.disastermod.events.server;

import com.a3ot.disastermod.events.AbstractEvent;
import com.a3ot.disastermod.events.utils.Utils;
import net.minecraft.ChatFormatting;
import net.minecraft.core.NonNullList;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class InventoryCrossEvent implements AbstractEvent {

    private static final int ARMOR_SLOTS_START = 36;
    private static final int ARMOR_SLOTS_END = 39;
    private static final int OFFHAND_SLOT = 40;
    private static final int MAIN_INVENTORY_END = 35;

    @Override
    public void onStart(ServerLevel level) {
        List<ServerPlayer> players = level.players().stream().filter(Utils::isValidPlayer).toList();

        if (players.isEmpty()) return;

        if (players.size() == 1) {
            level.players().stream().filter(Utils::isValidPlayer).forEach(player -> {
                Inventory inventory = player.getInventory();
                List<ItemStack> allItems = new ArrayList<>();

                allItems.addAll(inventory.items);
                allItems.addAll(inventory.armor);
                allItems.add(inventory.offhand.getFirst());
                Collections.shuffle(allItems);

                int slotIndex = 0;

                for (int i = 0; i < inventory.items.size(); i++) {
                    inventory.items.set(i, allItems.get(slotIndex));
                    slotIndex++;
                }
                for (int i = 0; i < inventory.armor.size(); i++) {
                    inventory.armor.set(i, allItems.get(slotIndex));
                    slotIndex++;
                }
                inventory.offhand.set(0, allItems.get(slotIndex));

                inventory.setChanged();
            });
        } else {
            List<NonNullList<ItemStack>> inventoryCopies = new ArrayList<>();
            for (ServerPlayer player : players) {
                NonNullList<ItemStack> copy = NonNullList.create();
                Inventory inv = player.getInventory();
                for (int i = 0; i <= MAIN_INVENTORY_END; i++) {
                    copy.add(inv.getItem(i).copy());
                }
                for (int i = ARMOR_SLOTS_START; i <= ARMOR_SLOTS_END; i++) {
                    copy.add(inv.getItem(i).copy());
                }
                copy.add(inv.getItem(OFFHAND_SLOT).copy());
                inventoryCopies.add(copy);
            }
            for (int i = 0; i < players.size(); i++) {
                ServerPlayer player = players.get(i);
                NonNullList<ItemStack> targetInventory = inventoryCopies.get((i + 1) % players.size());
                Inventory inv = player.getInventory();
                int slotIndex = 0;
                for (int j = 0; j <= MAIN_INVENTORY_END; j++) {
                    inv.setItem(j, targetInventory.get(slotIndex).copy());
                    slotIndex++;
                }
                for (int j = ARMOR_SLOTS_START; j <= ARMOR_SLOTS_END; j++) {
                    inv.setItem(j, targetInventory.get(slotIndex).copy());
                    slotIndex++;
                }
                inv.setItem(OFFHAND_SLOT, targetInventory.get(slotIndex).copy());
                inv.setChanged();
            }
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