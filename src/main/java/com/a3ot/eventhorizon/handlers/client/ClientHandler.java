package com.a3ot.eventhorizon.handlers.client;

import com.a3ot.eventhorizon.EventHorizon;
import com.a3ot.eventhorizon.config.ClientConfig;
import com.a3ot.eventhorizon.events.both.OldWalkingEvent;
import com.a3ot.eventhorizon.events.client.InvertedControlEvent;
import com.a3ot.eventhorizon.events.both.NoJumpEvent;
import com.a3ot.eventhorizon.network.NetworkHandler;
import com.a3ot.eventhorizon.network.packet.SoundConfigPacket;
import net.minecraft.client.player.Input;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.event.MovementInputUpdateEvent;

@EventBusSubscriber(modid = EventHorizon.MOD_ID, value = Dist.CLIENT)
public class ClientHandler {
    @SubscribeEvent
    public static void onMovementInput(MovementInputUpdateEvent event) {
        Input input = event.getInput();
        InvertedControlEvent.movementInput(input);
        NoJumpEvent.movementInput(input);
        OldWalkingEvent.movementInput(input);
    }

    @SubscribeEvent
    public static void onClientJoinInWorld(ClientPlayerNetworkEvent.LoggingIn event) {
        NetworkHandler.sendToServer(new SoundConfigPacket(ClientConfig.CLIENT_EVENT_SOUND.get(), ClientConfig.CLIENT_WARNING_SOUND.get()));
    }
}
