package com.a3ot.eventhorizon.network;

import com.a3ot.eventhorizon.EventHorizon;
import com.a3ot.eventhorizon.events.AbstractEvent;
import com.a3ot.eventhorizon.events.EventsRegistry;
import com.a3ot.eventhorizon.events.server.InventoryShuffleEvent;
import com.a3ot.eventhorizon.events.utils.Utils;
import com.a3ot.eventhorizon.handlers.ServerHandler;
import com.a3ot.eventhorizon.network.packet.*;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@EventBusSubscriber(modid = EventHorizon.MOD_ID)
public class NetworkHandler {

    @SubscribeEvent
    public static void onNetworkInit(RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar(EventHorizon.MOD_ID).optional();
        registerPackets(new NetworkRegisterStrategy() {
            @Override
            public <T extends CustomPacketPayload> void register(PacketInfo<T> packetInfo) {
                registrar.playBidirectional(packetInfo.type(), packetInfo.streamCodec(), (packet, context) -> context.enqueueWork(() -> packetInfo.handler().handle(packet, context.player())));
            }
        });
    }

    public static void sendToClient(ServerPlayer serverPlayer, CustomPacketPayload packet) {
        PacketDistributor.sendToPlayer(serverPlayer, packet);
    }

    public static void sendToServer(CustomPacketPayload packet) {
        PacketDistributor.sendToServer(packet);
    }

    public record PacketInfo<T extends CustomPacketPayload>(CustomPacketPayload.Type<T> type, StreamCodec<RegistryFriendlyByteBuf, T> streamCodec, Handler<T> handler) {

    }

    public interface NetworkRegisterStrategy {
        <T extends CustomPacketPayload> void register(PacketInfo<T> packetInfo);
    }

    public static void registerPackets(NetworkRegisterStrategy strategy) {
        strategy.register(new PacketInfo<>(SoundConfigPacket.TYPE, SoundConfigPacket.CODEC, (packet, player) -> {
            if (player instanceof ServerPlayer serverPlayer) {
                ServerHandler.setClientEventSound(serverPlayer, packet.enableSound());
                ServerHandler.setClientWarningSound(serverPlayer, packet.enableWarning());
            }
        }));
        strategy.register(new PacketInfo<>(ClientEventPacket.TYPE, ClientEventPacket.CODEC, (packet, player) -> {
            if (player.level().isClientSide()) {
                AbstractEvent event = EventsRegistry.getEvent(packet.eventName());
                if (event != null) {
                    switch (packet.eventType()) {
                        case START -> event.onClientStart(player.level());
                        case TICK -> event.onClientTick(player.level());
                        case END -> event.onClientEnd(player.level());
                    }
                }
            }
        }));
        strategy.register(new PacketInfo<>(ItemActivationPacket.TYPE, ItemActivationPacket.CODEC, (packet, player) -> {
            if (player.level().isClientSide()) Utils.playItemActivation(packet.itemStack(), packet.entityId());
        }));
        strategy.register(new PacketInfo<>(ShuffleRequestPacket.TYPE, ShuffleRequestPacket.CODEC, (packet, player) -> {
            if (player instanceof ServerPlayer serverPlayer) InventoryShuffleEvent.shuffle(serverPlayer);
        }));
        strategy.register(new PacketInfo<>(
                ClientVariableSyncPacket.TYPE,
                ClientVariableSyncPacket.CODEC,
                (packet, player) -> {
                    if (player.level().isClientSide()) {
                        com.a3ot.eventhorizon.handlers.client.ClientVariables.setVariable(
                                packet.variableName(),
                                packet.value()
                        );
                    }
                }
        ));
    }

    public interface Handler<T extends CustomPacketPayload> {
        void handle(T object, Player player);
    }
}

