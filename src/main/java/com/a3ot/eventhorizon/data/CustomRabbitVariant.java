package com.a3ot.eventhorizon.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

public record CustomRabbitVariant(ResourceLocation textureLocation) {

    public static final Codec<ResourceLocation> RESOURCE_LOCATION_CODEC = ResourceLocation.CODEC;

    public static final Codec<CustomRabbitVariant> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                    RESOURCE_LOCATION_CODEC.fieldOf("texture_location").forGetter(CustomRabbitVariant::textureLocation)
            ).apply(instance, CustomRabbitVariant::new)
    );

    public static final MapCodec<CustomRabbitVariant> MAP_CODEC = RecordCodecBuilder.mapCodec(
            instance -> instance.group(
                    RESOURCE_LOCATION_CODEC.fieldOf("texture_location").forGetter(CustomRabbitVariant::textureLocation)
            ).apply(instance, CustomRabbitVariant::new)
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, ResourceLocation> RESOURCE_LOCATION_STREAM_CODEC =
            ByteBufCodecs.fromCodecWithRegistries(RESOURCE_LOCATION_CODEC);

    public static final StreamCodec<RegistryFriendlyByteBuf, CustomRabbitVariant> STREAM_CODEC =
            StreamCodec.composite(
                    RESOURCE_LOCATION_STREAM_CODEC,
                    CustomRabbitVariant::textureLocation,
                    CustomRabbitVariant::new
            );
}