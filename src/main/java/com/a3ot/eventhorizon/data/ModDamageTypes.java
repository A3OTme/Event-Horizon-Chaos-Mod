package com.a3ot.eventhorizon.data;

import com.a3ot.eventhorizon.EventHorizon;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageType;

public class ModDamageTypes {
    public static final ResourceKey<DamageType> BOTANOPHOBIA = create("botanophobia");
    public static final ResourceKey<DamageType> NYCTOPHOBIA = create("nyctophobia");
    public static final ResourceKey<DamageType> HYDROPHOBIA = create("hydrophobia");

    public static ResourceKey<DamageType> create(String name) {
        return ResourceKey.create(Registries.DAMAGE_TYPE, ResourceLocation.fromNamespaceAndPath(EventHorizon.MOD_ID, name));
    }
}
