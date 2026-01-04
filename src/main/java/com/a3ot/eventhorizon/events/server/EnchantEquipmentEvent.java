package com.a3ot.eventhorizon.events.server;

import com.a3ot.eventhorizon.events.EventType;
import com.a3ot.eventhorizon.events.subclasses.AbstractInventoryEvent;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.EnchantmentTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.ItemEnchantments;

import java.util.List;

public class EnchantEquipmentEvent extends AbstractInventoryEvent implements AbstractInventoryEvent.ILevelAwareEvent{
    @Override
    public ItemStack modifyItem(ItemStack stack, ServerLevel level) {
        Registry<Enchantment> enchantmentRegistry = level.registryAccess().registryOrThrow(Registries.ENCHANTMENT);
        List<Holder<Enchantment>> enchantments = enchantmentRegistry.holders()
                .map(holder -> (Holder<Enchantment>) holder)
                .toList();
        if (stack.isEmpty() || !EnchantmentHelper.canStoreEnchantments(stack)) return stack;
        List<Holder<Enchantment>> applicableEnchantments = enchantments.stream()
                .filter(holder -> holder.value().canEnchant(stack))
                .filter(holder -> !holder.is(EnchantmentTags.CURSE))
                .toList();
        if (applicableEnchantments.isEmpty()) return stack;
        Holder<Enchantment> selected = applicableEnchantments.get(level.random.nextInt(applicableEnchantments.size()));
        Enchantment enchantment = selected.value();
        ItemEnchantments existing = stack.getTagEnchantments();
        if (existing.isEmpty()) {
            stack.enchant(selected, enchantment.getMaxLevel());
        } else {
            boolean compatible = existing.keySet().stream()
                    .allMatch(holder -> Enchantment.areCompatible(holder, selected));
            if (compatible) {
                stack.enchant(selected, enchantment.getMaxLevel());
            }
        }
        return stack;
    }

    @Override
    public ChatFormatting getColor() {
        return ChatFormatting.DARK_PURPLE;
    }

    @Override
    public SoundEvent getSound() {
        return SoundEvents.ENCHANTMENT_TABLE_USE;
    }

    @Override
    public EventType getType() {
        return EventType.POSITIVE;
    }

    @Override
    public double getDefaultDurationMultiplier() {
        return 0;
    }
}
