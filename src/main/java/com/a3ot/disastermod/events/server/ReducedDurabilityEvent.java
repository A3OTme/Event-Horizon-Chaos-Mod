package com.a3ot.disastermod.events.server;

import com.a3ot.disastermod.data.ModDataComponents;
import com.a3ot.disastermod.events.subclasses.AbstractDataComponentEvent;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;

public class ReducedDurabilityEvent extends AbstractDataComponentEvent{

    public ReducedDurabilityEvent() {
        AbstractDataComponentEvent.registerEvent(this);
    }

    public ItemStack modifyItem(ItemStack stack) {
        if (stack.isEmpty()) return stack;
        Integer originalMaxDamage = stack.get(ModDataComponents.ORIGINAL_MAX_DAMAGE.get());
        if (originalMaxDamage != null) return stack;

        Integer currentMaxDamage = stack.get(DataComponents.MAX_DAMAGE);
        if (currentMaxDamage == null || currentMaxDamage <= 0) return stack;

        Integer currentDamage = stack.get(DataComponents.DAMAGE);
        if (currentDamage == null || currentDamage <= 0) return stack;

        stack.set(ModDataComponents.ORIGINAL_MAX_DAMAGE.get(), currentMaxDamage);

        int reduced = Math.max(1, currentMaxDamage / 3);
        stack.set(DataComponents.MAX_DAMAGE, reduced);
        reduced = Math.max(1, currentDamage / 3);
        stack.set(DataComponents.DAMAGE, reduced);
        return stack;
    }

    public ItemStack restoreItem(ItemStack stack) {
        if (stack.isEmpty()) return stack;

        Integer originalMaxDamage = stack.get(ModDataComponents.ORIGINAL_MAX_DAMAGE.get());
        if (originalMaxDamage == null) return stack;

        stack.set(DataComponents.MAX_DAMAGE, originalMaxDamage);
        stack.remove(ModDataComponents.ORIGINAL_MAX_DAMAGE.get());

        Integer currentDamage = stack.get(DataComponents.DAMAGE);
        if (currentDamage == null || currentDamage <= 0) return stack;
        stack.set(DataComponents.DAMAGE, currentDamage * 3);
        return stack;
    }
}