package com.a3ot.disastermod.handlers.client;

import com.a3ot.disastermod.Disastermod;
import com.a3ot.disastermod.config.ClientConfig;
import com.a3ot.disastermod.events.client.InvertedControlEvent;
import com.a3ot.disastermod.events.both.NoJumpEvent;
import com.a3ot.disastermod.network.NetworkHandler;
import com.a3ot.disastermod.network.packet.SoundConfigPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.Input;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.event.MovementInputUpdateEvent;

@EventBusSubscriber(modid = Disastermod.MODID, value = Dist.CLIENT)
public class ClientHandler {
    @SubscribeEvent
    public static void onMovementInput(MovementInputUpdateEvent event) {
        Input input = event.getInput();
        InvertedControlEvent.movementInput(input);
        NoJumpEvent.movementInput(input);
    }

    @SubscribeEvent
    public static void onClientJoinInWorld(ClientPlayerNetworkEvent.LoggingIn event) {
        NetworkHandler.sendToServer(new SoundConfigPacket(ClientConfig.CLIENT_EVENT_SOUND.get(), ClientConfig.CLIENT_WARNING_SOUND.get()));
    }
}
