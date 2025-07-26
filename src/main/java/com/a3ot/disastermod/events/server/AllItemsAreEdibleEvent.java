package com.a3ot.disastermod.events.server;

import com.a3ot.disastermod.data.ModDataComponents;
import com.a3ot.disastermod.events.EventType;
import com.a3ot.disastermod.events.subclasses.AbstractDataComponentEvent;
import net.minecraft.core.component.DataComponents;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;

public class AllItemsAreEdibleEvent extends AbstractDataComponentEvent {

    public AllItemsAreEdibleEvent() {
        AbstractDataComponentEvent.registerEvent(this);
    }

    private static final FoodProperties EDIBLE_PROPERTIES = new FoodProperties.Builder()
            .nutrition(1)
            .saturationModifier(0.5f)
            .alwaysEdible()
            .build();

    public ItemStack modifyItem(ItemStack stack) {
        if (!stack.isEmpty() && !stack.has(DataComponents.FOOD)) {
            stack.set(DataComponents.FOOD, EDIBLE_PROPERTIES);
            stack.set(ModDataComponents.TEMPORARILY_EDIBLE.get(), true);
        }
        return stack;
    }

    public ItemStack restoreItem(ItemStack stack) {
        if (stack.has(ModDataComponents.TEMPORARILY_EDIBLE.get())) {
            stack.remove(DataComponents.FOOD);
            stack.remove(ModDataComponents.TEMPORARILY_EDIBLE.get());
        }
        return stack;
    }

    @Override
    public SoundEvent getSound(){
        return SoundEvents.PLAYER_BURP;
    }

    @Override
    public EventType getType() {
        return EventType.POSITIVE;
    }
}