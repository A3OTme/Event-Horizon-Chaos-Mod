package com.a3ot.disastermod.events.server;

import com.a3ot.disastermod.data.HiddenItemData;
import com.a3ot.disastermod.data.ModDataComponents;
import com.a3ot.disastermod.events.subclasses.AbstractInventoryEvent;
import com.a3ot.disastermod.events.subclasses.AbstractRandomSlotInventoryEvent;
import com.a3ot.disastermod.registry.ModItems;
import net.minecraft.core.NonNullList;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.event.entity.player.ItemEntityPickupEvent;

public class DementiaEvent extends AbstractRandomSlotInventoryEvent implements AbstractInventoryEvent.IBasicEvent {
    @Override
    protected int getSlotsToProcessCount() {
        return 1;
    }

    @Override
    protected boolean isSlotEligible(Player player, int slot, ItemStack stack) {
        return !stack.has(ModDataComponents.HIDDEN_ITEM.get()) && slot != player.getInventory().selected;
    }

    @Override
    protected boolean includeArmor() { return false; }

    @Override
    protected boolean includeOffhand() { return false; }

    @Override
    public ItemStack modifyItem(ItemStack stack) {
        ItemStack hidden = new ItemStack(ModItems.HIDDEN_ITEM_PLACEHOLDER.get());
        if (stack.isEmpty()) {
            hidden.set(ModDataComponents.HIDDEN_ITEM.get(), new HiddenItemData(ItemStack.EMPTY));
        } else {
            hidden.set(ModDataComponents.HIDDEN_ITEM.get(), new HiddenItemData(stack.copy()));
        }
        return hidden;
    }

    public static ItemStack removePlaceholder(ItemStack stack) {
        if (stack.isEmpty() || !stack.is(ModItems.HIDDEN_ITEM_PLACEHOLDER.get())) return stack;

        HiddenItemData hiddenItem = stack.get(ModDataComponents.HIDDEN_ITEM.get());
        if (hiddenItem == null || hiddenItem.isEmpty()) return ItemStack.EMPTY;

        return hiddenItem.itemStack();
    }

    public static void playerTick(Player player) {
        for (InteractionHand hand : InteractionHand.values()) {
            ItemStack currentStack = player.getItemInHand(hand);

            if (player.level().isClientSide() && currentStack.isEmpty()) continue;
            if (!currentStack.is(ModItems.HIDDEN_ITEM_PLACEHOLDER.get())) continue;

            ItemStack result = removePlaceholder(currentStack);
            if (!ItemStack.matches(currentStack, result)) {
                player.setItemInHand(hand, result);
                player.getInventory().setChanged();
            }
        }
    }

    public static void livingDeath(LivingEntity entity) {
        if (!(entity instanceof ServerPlayer player)) return;
        NonNullList<ItemStack> items = player.getInventory().items;
        for (int i = 0; i < items.size(); i++) {
            ItemStack stack = items.get(i);
            if (stack.is(ModItems.HIDDEN_ITEM_PLACEHOLDER.get())) {
                ItemStack result = removePlaceholder(stack);
                if (!ItemStack.matches(stack, result)) {
                    items.set(i, result);
                }
            }
        }
        player.getInventory().setChanged();
    }

    public static void itemPickup(ItemEntityPickupEvent event){
        ItemStack itemStack = event.getItemEntity().getItem();
        ItemStack result = DementiaEvent.removePlaceholder(itemStack);
        if (!ItemStack.matches(itemStack, result)) event.getItemEntity().setItem(result);
    }

    @Override
    public int getInterval() {
        return 60;
    }

    @Override
    public double getDefaultDurationMultiplier() {
        return 3;
    }
}
