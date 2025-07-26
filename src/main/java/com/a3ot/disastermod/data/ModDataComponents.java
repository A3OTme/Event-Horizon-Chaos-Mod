package com.a3ot.disastermod.data;

import com.a3ot.disastermod.Disastermod;
import com.mojang.serialization.Codec;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModDataComponents {
    public static final DeferredRegister<DataComponentType<?>> DATA_COMPONENT_TYPES =
            DeferredRegister.create(BuiltInRegistries.DATA_COMPONENT_TYPE, Disastermod.MODID);

    public static final Supplier<DataComponentType<HiddenItemData>> HIDDEN_ITEM =
            DATA_COMPONENT_TYPES.register(
                    "hidden_item",
                    () -> DataComponentType.<HiddenItemData>builder()
                            .persistent(HiddenItemData.Codec.CODEC)
                            .networkSynchronized(HiddenItemData.Stream.STREAM_CODEC)
                            .build()
            );

    public static final Supplier<DataComponentType<Integer>> ORIGINAL_MAX_DAMAGE =
            DATA_COMPONENT_TYPES.register(
            "original_max_damage",
                    () -> DataComponentType.<Integer>builder()
                            .persistent(com.mojang.serialization.Codec.INT)
                            .networkSynchronized(ByteBufCodecs.VAR_INT)
                            .build()
            );

    public static final Supplier<DataComponentType<Boolean>> TEMPORARILY_EDIBLE =
            DATA_COMPONENT_TYPES.register(
                    "temporarily_edible",
                    () -> DataComponentType.<Boolean>builder()
                            .persistent(Codec.BOOL)
                            .networkSynchronized(ByteBufCodecs.BOOL)
                            .build()
            );
}
