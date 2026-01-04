package com.a3ot.eventhorizon.data;

import com.a3ot.eventhorizon.EventHorizon;
import com.mojang.serialization.Codec;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;

public class ModDataComponents {
    public static final DeferredRegister<DataComponentType<?>> DATA_COMPONENT_TYPES =
            DeferredRegister.create(BuiltInRegistries.DATA_COMPONENT_TYPE, EventHorizon.MOD_ID);

    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES =
            DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, EventHorizon.MOD_ID);

    public static final Supplier<AttachmentType<CustomRabbitVariant>> CUSTOM_RABBIT_VARIANT =
            ATTACHMENT_TYPES.register(
                    "custom_rabbit_variant",
                    () -> AttachmentType.builder(() -> new CustomRabbitVariant(ResourceLocation.fromNamespaceAndPath(EventHorizon.MOD_ID, "textures/entity/rabbit/diamond.png")))
                            .serialize(CustomRabbitVariant.CODEC)
                            .sync(CustomRabbitVariant.STREAM_CODEC)
                            .build()
            );

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

    public static final Supplier<DataComponentType<Boolean>> IS_SPICED_FOOD =
            DATA_COMPONENT_TYPES.register(
                    "is_spiced_food",
                    () -> DataComponentType.<Boolean>builder()
                            .persistent(Codec.BOOL)
                            .networkSynchronized(ByteBufCodecs.BOOL)
                            .build()
            );
}
