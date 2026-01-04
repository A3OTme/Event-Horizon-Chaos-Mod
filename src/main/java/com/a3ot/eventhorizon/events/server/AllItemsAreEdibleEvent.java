package com.a3ot.eventhorizon.events.server;

import com.a3ot.eventhorizon.data.ModDataComponents;
import com.a3ot.eventhorizon.events.EventType;
import com.a3ot.eventhorizon.events.subclasses.AbstractDataComponentEvent;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;

public class AllItemsAreEdibleEvent extends AbstractDataComponentEvent {

    public AllItemsAreEdibleEvent() {
        AbstractDataComponentEvent.registerEvent(this);
    }

    private static final FoodProperties EDIBLE_PROPERTIES = new FoodProperties.Builder()
            .nutrition(4)
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
    public void onTick(ServerLevel level) {
        MobEffectInstance hunger = new MobEffectInstance(MobEffects.HUNGER, 200, 9, false, false);
        level.players().forEach(player ->player.addEffect(hunger));
        super.onTick(level);
    }

    @Override
    public void onEnd(ServerLevel level) {
        level.players().forEach(player -> player.removeEffect(MobEffects.HUNGER));
        super.onEnd(level);
    }

    @Override
    public SoundEvent getSound(){
        return SoundEvents.PLAYER_BURP;
    }

    @Override
    public EventType getType() {
        return EventType.NEUTRAL;
    }
}