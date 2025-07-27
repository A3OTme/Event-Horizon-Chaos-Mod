package com.a3ot.disastermod.events.subclasses;

import com.a3ot.disastermod.Disastermod;
import com.a3ot.disastermod.data.ModDataComponents;
import com.a3ot.disastermod.data.ModTags;
import com.a3ot.disastermod.events.server.ReducedDurabilityEvent;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.component.ItemAttributeModifiers;
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

        Integer currentMaxDamage = stack.get(ModDataComponents.ORIGINAL_MAX_DAMAGE.get());
        if (currentMaxDamage != null) {
            stack.set(DataComponents.MAX_DAMAGE, currentMaxDamage);
            stack.remove(ModDataComponents.ORIGINAL_MAX_DAMAGE.get());

            Integer currentDamage = stack.get(DataComponents.DAMAGE);
            if (currentDamage == null || currentDamage <= 0) return stack;
            stack.set(DataComponents.DAMAGE, currentDamage * 3);
        }

        ItemStack transformed = new ItemStack(newItem.get(), stack.getCount());

        DataComponentPatch originalPatch = stack.getComponentsPatch();
        transformed.applyComponents(originalPatch);

        boolean isNotCursedItem = true;
        CustomData customDataComponent = stack.get(DataComponents.CUSTOM_DATA);
        if (customDataComponent != null) {
            CompoundTag customDataTag = customDataComponent.getUnsafe();
            isNotCursedItem = !customDataTag.getBoolean("cursed_item");
        }

        if (isNotCursedItem){
            int maxDamage = transformed.getMaxDamage();
            int scaledDamage = Math.min(
                    (int) ((double) stack.getDamageValue() / stack.getMaxDamage() * maxDamage),
                    maxDamage - 1
            );
            transformed.setDamageValue(scaledDamage);
        }

        ItemEnchantments enchantments = stack.getTagEnchantments();
        EnchantmentHelper.setEnchantments(transformed, enchantments);

        ItemAttributeModifiers originalAttributes = stack.getOrDefault(DataComponents.ATTRIBUTE_MODIFIERS, ItemAttributeModifiers.EMPTY);
        transformed.set(DataComponents.ATTRIBUTE_MODIFIERS, originalAttributes);

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

    @Override
    public double getDefaultDurationMultiplier() {
        return 0;
    }
}
