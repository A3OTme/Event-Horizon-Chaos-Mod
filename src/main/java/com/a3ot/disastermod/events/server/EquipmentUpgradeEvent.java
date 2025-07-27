package com.a3ot.disastermod.events.server;

import com.a3ot.disastermod.events.EventType;
import com.a3ot.disastermod.events.subclasses.AbstractEquipmentTransformEvent;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.*;

import java.util.*;


public class EquipmentUpgradeEvent extends AbstractEquipmentTransformEvent {
    private static final Map<Tier, Tier> NEXT_TIER = Map.of(
            Tiers.WOOD, Tiers.STONE,
            Tiers.GOLD, Tiers.STONE,
            Tiers.STONE, Tiers.IRON,
            Tiers.IRON, Tiers.DIAMOND,
            Tiers.DIAMOND, Tiers.NETHERITE
    );

    private static final Map<Holder<ArmorMaterial>, Holder<ArmorMaterial>> NEXT_MATERIAL = Map.of(
            ArmorMaterials.LEATHER, ArmorMaterials.GOLD,
            ArmorMaterials.GOLD, ArmorMaterials.CHAIN,
            ArmorMaterials.CHAIN, ArmorMaterials.IRON,
            ArmorMaterials.IRON, ArmorMaterials.DIAMOND,
            ArmorMaterials.DIAMOND, ArmorMaterials.NETHERITE
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
        return NEXT_TIER;
    }

    @Override
    protected Map<Holder<ArmorMaterial>, Holder<ArmorMaterial>> getMaterialMap() {
        return NEXT_MATERIAL;
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
        return ChatFormatting.DARK_AQUA;
    }

    @Override
    public SoundEvent getSound() {
        return SoundEvents.SMITHING_TABLE_USE;
    }

    @Override
    public EventType getType() {
        return EventType.POSITIVE;
    }
}