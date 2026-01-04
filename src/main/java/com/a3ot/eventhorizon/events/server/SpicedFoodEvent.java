package com.a3ot.eventhorizon.events.server;

import com.a3ot.eventhorizon.data.ModDataComponents;
import com.a3ot.eventhorizon.events.EventType;
import com.a3ot.eventhorizon.events.subclasses.AbstractInventoryEvent;
import com.a3ot.eventhorizon.events.utils.Utils;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class SpicedFoodEvent extends AbstractInventoryEvent implements AbstractInventoryEvent.IBasicEvent {
    private static final String SPICE_SUFFIX = "\uD83C\uDF56";//🍖
    private static boolean hadFood = false;

    @Override
    public void onStart(ServerLevel level) {
        for (ServerPlayer player : level.players()) {
            if (!Utils.isValidPlayer(player)) continue;
            processAllSlots(level, player);
            if (!hadFood) {
                ItemStack breadStack = new ItemStack(Items.BREAD, 16);
                ItemStack processedStack = modifyItem(breadStack);
                if (processedStack != null) Utils.giveItem(player, processedStack);
            }
            hadFood = false;
        }
    }

    @Override
    public ItemStack modifyItem(ItemStack stack) {
        if (!stack.isEmpty() && stack.has(DataComponents.FOOD) && !stack.has(ModDataComponents.IS_SPICED_FOOD.get())) {
            FoodProperties originalFood = stack.get(DataComponents.FOOD);
            if (originalFood != null) {
                hadFood = true;
                ItemStack newStack = stack.copy();
                int newNutrition = (int) (originalFood.nutrition() * 1.5F);
                FoodProperties.Builder foodBuilder = new FoodProperties.Builder()
                        .nutrition(newNutrition)
                        .saturationModifier(originalFood.saturation() / newNutrition);
                if (originalFood.canAlwaysEat()) {
                    foodBuilder.alwaysEdible();
                }
                FoodProperties newFoodProperties = foodBuilder.build();
                newStack.set(DataComponents.FOOD, newFoodProperties);
                Component originalName = stack.get(DataComponents.CUSTOM_NAME);
                if (originalName == null) originalName = stack.getItem().getName(stack).copy().withStyle(Style.EMPTY.withItalic(false));
                Component suffixComponent = Component.literal(" " + SPICE_SUFFIX).withStyle(Style.EMPTY.withItalic(false).withColor(ChatFormatting.GOLD));
                Component newName = Component.empty().append(originalName).append(suffixComponent);
                newStack.set(DataComponents.CUSTOM_NAME, newName);

                newStack.set(ModDataComponents.IS_SPICED_FOOD.get(), true);

                return newStack;
            }
        }
        return stack;
    }

    @Override
    public EventType getType() {
        return EventType.POSITIVE;
    }

    @Override
    public ChatFormatting getColor() {
        return ChatFormatting.GOLD;
    }

    @Override
    public SoundEvent getSound() {
        return SoundEvents.LAVA_EXTINGUISH;
    }

    @Override
    public double getDefaultDurationMultiplier() {
        return 0;
    }
}