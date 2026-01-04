package com.a3ot.eventhorizon.events.server;

import com.a3ot.eventhorizon.events.subclasses.AbstractEquipmentTransformEvent;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.*;

import java.util.*;

public class EquipmentDowngradeEvent extends AbstractEquipmentTransformEvent {
    private static final Map<Tier, Tier> PREV_TIER = Map.of(
            Tiers.STONE, Tiers.WOOD,
            Tiers.IRON, Tiers.STONE,
            Tiers.DIAMOND, Tiers.IRON,
            Tiers.NETHERITE, Tiers.DIAMOND
    );

    private static final Map<Holder<ArmorMaterial>, Holder<ArmorMaterial>> PREV_MATERIAL = Map.of(
            ArmorMaterials.GOLD, ArmorMaterials.LEATHER,
            ArmorMaterials.CHAIN, ArmorMaterials.GOLD,
            ArmorMaterials.IRON, ArmorMaterials.CHAIN,
            ArmorMaterials.DIAMOND, ArmorMaterials.IRON,
            ArmorMaterials.NETHERITE, ArmorMaterials.DIAMOND
    );

    private static final Map<Tier, String> TIER_NAMES = Map.of(
            Tiers.WOOD, "wooden",
            Tiers.STONE, "stone",
            Tiers.GOLD, "golden",
            Tiers.IRON, "iron",
            Tiers.DIAMOND, "diamond",
            Tiers.NETHERITE, "netherite"
    );

    private static final Map<Holder<ArmorMaterial>, String> MATERIAL_NAMES = Map.of(
            ArmorMaterials.LEATHER, "leather",
            ArmorMaterials.GOLD, "golden",
            ArmorMaterials.CHAIN, "chainmail",
            ArmorMaterials.IRON, "iron",
            ArmorMaterials.DIAMOND, "diamond",
            ArmorMaterials.NETHERITE, "netherite"
    );

    @Override
    protected Map<Tier, Tier> getTierMap() {
        return PREV_TIER;
    }

    @Override
    protected Map<Holder<ArmorMaterial>, Holder<ArmorMaterial>> getMaterialMap() {
        return PREV_MATERIAL;
    }

    @Override
    protected Map<Tier, String> getTierNames() {
        return TIER_NAMES;
    }

    @Override
    protected Map<Holder<ArmorMaterial>, String> getMaterialNames() {
        return MATERIAL_NAMES;
    }

    @Override
    public ChatFormatting getColor() {
        return ChatFormatting.BLUE;
    }

    @Override
    public SoundEvent getSound() {
        return SoundEvents.GRINDSTONE_USE;
    }
}
