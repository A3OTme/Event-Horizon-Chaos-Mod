package com.a3ot.disastermod.network.packet;

import com.a3ot.disastermod.Disastermod;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public record SoundConfigPacket(boolean enableSound, boolean enableWarning) implements CustomPacketPayload {
    public static final Type<SoundConfigPacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(Disastermod.MODID, "sound_config"));

    public static final StreamCodec<RegistryFriendlyByteBuf, SoundConfigPacket> CODEC = StreamCodec.of(
            (buf, packet) -> {
                buf.writeBoolean(packet.enableSound());
                buf.writeBoolean(packet.enableWarning());
            },
            buf -> new SoundConfigPacket(
                    buf.readBoolean(),
                    buf.readBoolean()
            )
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
