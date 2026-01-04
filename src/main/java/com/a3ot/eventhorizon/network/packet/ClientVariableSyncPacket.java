package com.a3ot.eventhorizon.network.packet;

import com.a3ot.eventhorizon.EventHorizon;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public record ClientVariableSyncPacket(String variableName, boolean value) implements CustomPacketPayload {

    public static final Type<ClientVariableSyncPacket> TYPE = new Type<>(
            ResourceLocation.fromNamespaceAndPath(EventHorizon.MOD_ID, "client_var_sync")
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, ClientVariableSyncPacket> CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8,
            ClientVariableSyncPacket::variableName,
            ByteBufCodecs.BOOL,
            ClientVariableSyncPacket::value,
            ClientVariableSyncPacket::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
