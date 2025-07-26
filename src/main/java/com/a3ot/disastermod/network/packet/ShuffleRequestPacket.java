package com.a3ot.disastermod.network.packet;

import com.a3ot.disastermod.Disastermod;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public record ShuffleRequestPacket() implements CustomPacketPayload {
    public static final Type<ShuffleRequestPacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(Disastermod.MODID, "shuffle_request"));

    public static final StreamCodec<RegistryFriendlyByteBuf, ShuffleRequestPacket> CODEC = StreamCodec.unit(new ShuffleRequestPacket());

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}