package com.a3ot.disastermod.registry;

import net.minecraft.core.component.DataComponents;
import net.minecraft.world.food.Foods;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems("disastermod");

    public static final DeferredItem<Item> HIDDEN_ITEM_PLACEHOLDER = ITEMS.registerItem(
            "hidden_item_placeholder",
            HiddenItem::new,
            new Item.Properties().stacksTo(1)
    );

    public static final DeferredItem<Item> ENCHANTED_HONEY_BOTTLE = ITEMS.registerItem(
            "enchanted_honey_bottle",
            EnchantedHoneyBottleItem::new,
            new Item.Properties().rarity(Rarity.EPIC)
                    .component(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, true)
                    .food(Foods.HONEY_BOTTLE).stacksTo(16)
    );
}
