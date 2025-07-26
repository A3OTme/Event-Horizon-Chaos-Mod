package com.a3ot.disastermod.gui;

import com.a3ot.disastermod.ServerTick;
import com.a3ot.disastermod.events.server.UnknownEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerPlayer;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TimerBossBar {
    private static ServerBossEvent bossBar;
    private static final List<UUID> trackedPlayers = new ArrayList<>();

    public static void update(int tickCounter, int totalTicks) {
        if (bossBar != null) {
            bossBar.setVisible(!UnknownEvent.isActive());
            int secondsLeft = (totalTicks - tickCounter) / 20 + 1;
            bossBar.setName(Component.translatable("disastermod.timer.bossbar.message", secondsLeft));
            bossBar.setProgress(tickCounter / (float) totalTicks);
        }
    }

    public static void setBossBarColor(boolean bool) {
        if (bossBar != null) {
            ServerBossEvent.BossBarColor color = bool ? ServerBossEvent.BossBarColor.GREEN : ServerBossEvent.BossBarColor.BLUE;
            bossBar.setColor(color);
        }
    }

    public static void initialize(MinecraftServer server) {
        if (bossBar == null && server != null) {
            int totalTicks = ServerTick.updateTotalTicks();
            bossBar = new ServerBossEvent(
                    Component.translatable("disastermod.timer.bossbar.message", totalTicks / 20),
                    ServerBossEvent.BossBarColor.BLUE,
                    ServerBossEvent.BossBarOverlay.PROGRESS
            );
            bossBar.setProgress(0);
            server.getPlayerList().getPlayers().forEach(player -> {
                bossBar.addPlayer(player);
                trackedPlayers.add(player.getUUID());
            });
        }
    }

    public static void deinitialize(MinecraftServer server) {
        if (bossBar != null && server != null) {
            trackedPlayers.forEach(uuid -> {
                ServerPlayer player = server.getPlayerList().getPlayer(uuid);
                if (player != null) {
                    bossBar.removePlayer(player);
                }
            });
            trackedPlayers.clear();
            bossBar = null;
        }
    }

    public static void addPlayerToBossBar(ServerPlayer player) {
        if (bossBar != null) {
            bossBar.addPlayer(player);
            trackedPlayers.add(player.getUUID());
        }
    }

    public static void removePlayerFromBossBar(UUID uuid) {
        trackedPlayers.remove(uuid);
    }
}
