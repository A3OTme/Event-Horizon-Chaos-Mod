package com.a3ot.disastermod.handlers;

import com.a3ot.disastermod.Disastermod;
import com.a3ot.disastermod.ServerTick;
import com.a3ot.disastermod.api.event.PlayerChangeGameModeEvent;
import com.a3ot.disastermod.events.AbstractEvent;
import com.a3ot.disastermod.events.EventSide;
import com.a3ot.disastermod.gui.TimerBossBar;
import com.a3ot.disastermod.network.packet.ClientEventPacket;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.server.ServerStoppingEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@EventBusSubscriber(modid = Disastermod.MODID)
public class ServerHandler {
    private static final Map<UUID, Boolean> CLIENT_EVENT_SOUND_MAP = new HashMap<>();
    private static final Map<UUID, Boolean> CLIENT_WARNING_SOUND_MAP = new HashMap<>();

    @SubscribeEvent
    public static void onServerStopping(ServerStoppingEvent event) {
        ServerTick.pause(event.getServer());
    }

    @SubscribeEvent
    public static void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        if (!ServerTick.isEnabled()) return;
        sendMessage(player);
        TimerBossBar.addPlayerToBossBar(player);
        if (!ServerTick.isRun()) return;
        syncActiveEventsWithPlayer(player);
    }

    public static void syncActiveEventsWithPlayer(ServerPlayer player) {
        for (Map.Entry<AbstractEvent, Integer> entry : ServerTick.getActiveEvents().entrySet()) {
            AbstractEvent event = entry.getKey();
            if (event.getSide() != EventSide.CLIENT) event.playerRespawnOrJoin(player, player.level());
            if (event.getSide() != EventSide.SERVER) ClientEventPacket.sendClientEventPacket(event, ClientEventPacket.EventType.START, player.server);
        }
    }

    @SubscribeEvent
    public static void onPlayerChangeGameMode(PlayerChangeGameModeEvent.Post event) {
        if (!ServerTick.isRun()) return;
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        player.setGameMode(event.getNewGameMode());
        syncActiveEventsWithPlayer(player);
    }

    public static void sendMessage(ServerPlayer player){
        if (!ServerTick.isRun()){
            MutableComponent command = Component.translatable("disastermod.message.start_command")
                    .withStyle(ChatFormatting.GREEN)
                    .withStyle(style -> style
                            .withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/event start")))
                    .withStyle(style -> style
                            .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                                    Component.literal("/event start"))));
            MutableComponent message = Component.translatable("disastermod.message.ready", command);
            player.sendSystemMessage(message);
        }
    }

    @SubscribeEvent
    public static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        Player player = event.getEntity();
        Level level = player.level();
        for (AbstractEvent abstractEvent : ServerTick.getActiveEvents().keySet()) {
            Disastermod.LOGGER.info(abstractEvent.getConfigName());
            if (abstractEvent.getSide() != EventSide.CLIENT) abstractEvent.playerRespawnOrJoin(player, level);
        }
    }

    @SubscribeEvent
    public static void onPlayerLeave(PlayerEvent.PlayerLoggedOutEvent event) {
        ServerPlayer serverPlayer = (ServerPlayer) event.getEntity();
        TimerBossBar.removePlayerFromBossBar(serverPlayer.getUUID());
        UUID uuid = serverPlayer.getUUID();
        CLIENT_EVENT_SOUND_MAP.remove(uuid);
        CLIENT_WARNING_SOUND_MAP.remove(uuid);
    }

    public static void setClientEventSound(ServerPlayer player, boolean eventSound) {
        CLIENT_EVENT_SOUND_MAP.put(player.getUUID(), eventSound);
    }

    public static boolean isClientEventSound(ServerPlayer player) {
        return CLIENT_EVENT_SOUND_MAP.getOrDefault(player.getUUID(), true);
    }

    public static void setClientWarningSound(ServerPlayer player, boolean warningSound) {
        CLIENT_WARNING_SOUND_MAP.put(player.getUUID(), warningSound);
    }

    public static boolean isClientWarningSound(ServerPlayer player) {
        return CLIENT_WARNING_SOUND_MAP.getOrDefault(player.getUUID(), true);
    }
}