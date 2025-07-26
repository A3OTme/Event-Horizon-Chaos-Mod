package com.a3ot.disastermod.network.packet;

import com.a3ot.disastermod.Disastermod;
import com.a3ot.disastermod.events.AbstractEvent;
import com.a3ot.disastermod.events.utils.Utils;
import com.a3ot.disastermod.network.NetworkHandler;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import org.jetbrains.annotations.NotNull;

public record ClientEventPacket(String eventName, EventType eventType) implements CustomPacketPayload {
    public enum EventType { START, TICK, END }

    public static final Type<ClientEventPacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(Disastermod.MODID, "client_event"));

    public static final StreamCodec<RegistryFriendlyByteBuf, ClientEventPacket> CODEC = StreamCodec.of(
            (buf, packet) -> {
                buf.writeUtf(packet.eventName());
                buf.writeEnum(packet.eventType());
            },
            buf -> new ClientEventPacket(buf.readUtf(), buf.readEnum(EventType.class))
    );

    public static void sendClientEventPacket(AbstractEvent event, ClientEventPacket.EventType type, MinecraftServer server) {
        String eventName = event.getConfigName();
        if (type == EventType.END) {
            server.getPlayerList().getPlayers().forEach(player ->
                    NetworkHandler.sendToClient(player, new ClientEventPacket(eventName, EventType.END))
            );
        } else {
            server.getPlayerList().getPlayers().forEach(player -> {
                if (Utils.isPlayerValid(player)) {
                    NetworkHandler.sendToClient(player, new ClientEventPacket(eventName, type));
                } else {
                    NetworkHandler.sendToClient(player, new ClientEventPacket(eventName, EventType.END));
                }
            });
        }
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}