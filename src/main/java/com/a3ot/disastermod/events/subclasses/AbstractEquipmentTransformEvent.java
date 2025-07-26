package com.a3ot.disastermod.events.subclasses;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.*;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.ItemEnchantments;

import java.util.*;

public abstract class AbstractEquipmentTransformEvent extends AbstractInventoryEvent implements AbstractInventoryEvent.IBasicEvent {
    protected abstract Map<Tier, Tier> getTierMap();
    protected abstract Map<Holder<ArmorMaterial>, Holder<ArmorMaterial>> getMaterialMap();
    protected abstract Map<Tier, String> getTierNames();
    protected abstract Map<Holder<ArmorMaterial>, String> getMaterialNames();

    @Override
    public ItemStack modifyItem(ItemStack stack) {
        if (stack.isEmpty()) return stack;

        Item originalItem = stack.getItem();
        Optional<Item> newItem = Optional.empty();

        if (originalItem instanceof TieredItem tieredItem) {
            Tier currentTier = tieredItem.getTier();
            Tier nextTier = getTierMap().get(currentTier);
            if (nextTier != null) {
                newItem = getTransformedItem(originalItem, getTierNames().get(nextTier));
            }
        } else if (originalItem instanceof ArmorItem armorItem) {
            Holder<ArmorMaterial> currentMaterial = armorItem.getMaterial();
            Holder<ArmorMaterial> nextMaterial = getMaterialMap().get(currentMaterial);
            if (nextMaterial != null) {
                newItem = getTransformedItem(originalItem, getMaterialNames().get(nextMaterial));
            }
        }

        if (newItem.isEmpty()) return stack;

        ItemEnchantments enchantments = stack.getTagEnchantments();
        ItemStack transformed = new ItemStack(newItem.get(), stack.getCount());

        int maxDamage = transformed.getMaxDamage();
        int scaledDamage = Math.min(
                (int) ((double) stack.getDamageValue() / stack.getMaxDamage() * maxDamage),
                maxDamage - 1
        );
        transformed.setDamageValue(scaledDamage);
        EnchantmentHelper.setEnchantments(transformed, enchantments);

        return transformed;
    }

    protected Optional<Item> getTransformedItem(Item original, String targetMaterialName) {
        ResourceLocation originalId = BuiltInRegistries.ITEM.getKey(original);
        String originalName = originalId.getPath();

        int underscoreIndex = originalName.indexOf('_');
        if (underscoreIndex == -1) return Optional.empty();

        String armorType = originalName.substring(underscoreIndex);
        String nextItemName = targetMaterialName + armorType;

        ResourceLocation nextItemId = ResourceLocation.tryParse(originalId.getNamespace() + ":" + nextItemName);
        if (nextItemId == null) return Optional.empty();

        Item transformedItem = BuiltInRegistries.ITEM.get(nextItemId);
        return Optional.of(transformedItem).filter(item -> item != Items.AIR);
    }
}
