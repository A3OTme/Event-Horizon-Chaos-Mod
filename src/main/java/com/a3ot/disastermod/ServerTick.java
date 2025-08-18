package com.a3ot.disastermod;

import com.a3ot.disastermod.config.GeneralConfig;
import com.a3ot.disastermod.events.AbstractEvent;
import com.a3ot.disastermod.events.EventSide;
import com.a3ot.disastermod.events.EventsRegistry;
import com.a3ot.disastermod.events.server.*;
import com.a3ot.disastermod.events.utils.IActiveStateEvent;
import com.a3ot.disastermod.gui.ActiveEventScoreBoard;
import com.a3ot.disastermod.gui.TimerBossBar;
import com.a3ot.disastermod.handlers.ServerHandler;
import com.a3ot.disastermod.network.packet.ClientEventPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;

import java.util.*;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@EventBusSubscriber
public class ServerTick {
    private static int tickCounter = 0;
    private static int totalTicks = 1200;
    private static boolean isRunning = false;
    private static final Map<AbstractEvent, Integer> ACTIVE_EVENTS = new HashMap<>();
    private static final ThreadLocalRandom random = ThreadLocalRandom.current();
    private static final Set<Integer> warningTicks = Set.of(20, 40, 60, 80, 100);

    @SubscribeEvent
    private static void onServerTick(ServerTickEvent.Post event) {
        MinecraftServer server = event.getServer();
        if (checkModEnabledStatus(server)) return;
        if (!isRunning) return;
        checkAndTriggerNewEvent(server);
        activeEventsTick(server);
        checkWarningTicks(server);
        TimerBossBar.update(tickCounter, totalTicks);
        tickCounter++;
    }

    private static boolean wasEnabled = false;
    private static boolean checkModEnabledStatus(MinecraftServer server) {
        boolean isEnabled = isEnabled();
        if (isEnabled != wasEnabled) {
            if (isEnabled) {
                TimerBossBar.initialize(server);
                ActiveEventScoreBoard.initialize(server);
            } else {
                TimerBossBar.deinitialize(server);
                ActiveEventScoreBoard.deinitialize(server);
                resetTimerAndEvents();
            }
            wasEnabled = isEnabled;
        }
        return !isEnabled;
    }

    private static void resetTimerAndEvents() {
        isRunning = false;
        tickCounter = 0;
        ACTIVE_EVENTS.clear(); //todo Очистить эффекты и только потом очистить Map
    }

    private static void checkWarningTicks(MinecraftServer server) {
        if (!UnknownEvent.isActive() && warningTicks.contains(totalTicks - tickCounter)) {
            server.getPlayerList().getPlayers().forEach(player -> {
                if (ServerHandler.isClientWarningSound(player)) {
                    player.playNotifySound(
                            SoundEvents.LEVER_CLICK,
                            SoundSource.MASTER,
                            1.0F,
                            1.0F
                    );
                }
            });
        }
    }

    private static void checkAndTriggerNewEvent(MinecraftServer server) {
        if (tickCounter >= totalTicks) {
            List<AbstractEvent> enabledEvents = getFilteredEvents();
            if (!enabledEvents.isEmpty()) {
                AbstractEvent randomEvent = enabledEvents.get(random.nextInt(enabledEvents.size()));
                addActiveEvent(randomEvent, server);
                broadcastEventStart(randomEvent, server);
            }
            tickCounter = 0;
        }
    }

    private static List<AbstractEvent> getFilteredEvents() {
        if (OnlyPositiveEventsEvent.isActive()) return EventsRegistry.getPositiveEvents();
        if (OnlyNegativeEventsEvent.isActive()) return EventsRegistry.getNegativeEvents();
        return EventsRegistry.getEnabledEvents();
    }

    private static void broadcastEventStart(AbstractEvent event, MinecraftServer server) {
        server.getAllLevels().forEach(level -> {
            if (event.getSide() != EventSide.CLIENT) event.onStart(level);
            if (event.getSide() != EventSide.SERVER) ClientEventPacket.sendClientEventPacket(event, ClientEventPacket.EventType.START, server);
            if (!UnknownEvent.isActive() || event instanceof UnknownEvent) event.sendEventNotification(level, event);
        });
    }

    public static void addActiveEvent(AbstractEvent newEvent, MinecraftServer server) {
        double multiplier = newEvent.getDurationMultiplier();
        int x2EventMultiplier = X2EventTimeEvent.isActive() ? 2 : 1;
        if (multiplier <= 0) return;
        int durationTicks = (int) (GeneralConfig.TIMER_DURATION.get() * 20 * multiplier * x2EventMultiplier);

        Iterator<Map.Entry<AbstractEvent, Integer>> iterator = ACTIVE_EVENTS.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<AbstractEvent, Integer> entry = iterator.next();
            if (newEvent.conflictsWith(entry.getKey())) {
                iterator.remove();
                AbstractEvent event = entry.getKey();
                if (event.getSide() != EventSide.CLIENT) server.getAllLevels().forEach(event::onEnd);
                if (event.getSide() != EventSide.SERVER) ClientEventPacket.sendClientEventPacket(event, ClientEventPacket.EventType.END, server);
            }
        }

        ACTIVE_EVENTS.compute(newEvent, (event, currentDuration) -> {
            if (currentDuration == null) {
                return durationTicks;
            } else {
                return currentDuration + durationTicks;
            }
        });
    }

    public static void addActiveEventWithCustomDuration(AbstractEvent newEvent, int durationSeconds, MinecraftServer server) {
        if (newEvent.getDurationMultiplier() <= 0) return;
        int durationTicks = durationSeconds * 20;
        Iterator<Map.Entry<AbstractEvent, Integer>> iterator = ACTIVE_EVENTS.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<AbstractEvent, Integer> entry = iterator.next();
            if (newEvent.conflictsWith(entry.getKey())) {
                iterator.remove();
                AbstractEvent event = entry.getKey();
                if (event.getSide() != EventSide.CLIENT) server.getAllLevels().forEach(event::onEnd);
                if (event.getSide() != EventSide.SERVER) ClientEventPacket.sendClientEventPacket(event, ClientEventPacket.EventType.END, server);
            }
        }
        ACTIVE_EVENTS.compute(newEvent, (event, currentDuration) -> {
            if (currentDuration == null) {
                return durationTicks;
            } else {
                return currentDuration + durationTicks;
            }
        });
    }

    private static void activeEventsTick(MinecraftServer server) {
        int currentTick = tickCounter;
        for (Iterator<Map.Entry<AbstractEvent, Integer>> iterator = ACTIVE_EVENTS.entrySet().iterator(); iterator.hasNext();) {
            Map.Entry<AbstractEvent, Integer> entry = iterator.next();
            AbstractEvent event = entry.getKey();
            Integer duration = entry.getValue();

            if (duration == null || duration <= 0) {
                iterator.remove();
                if (event.getSide() != EventSide.CLIENT) server.getAllLevels().forEach(event::onEnd);
                if (event.getSide() != EventSide.SERVER) ClientEventPacket.sendClientEventPacket(event, ClientEventPacket.EventType.END, server);
            } else {
                if (currentTick % event.getInterval() == 0) {
                    if (event.getSide() != EventSide.CLIENT && event.requiresPeriodicTick()) server.getAllLevels().forEach(event::onTick);
                    if (event.getSide() != EventSide.SERVER && event.requiresPeriodicClientTick()) ClientEventPacket.sendClientEventPacket(event, ClientEventPacket.EventType.TICK, server);
                }
                entry.setValue(duration - 1);
            }
        }
        ActiveEventScoreBoard.update(server);
    }

    public static int updateTotalTicks() {
        int newTotalTicks = GeneralConfig.TIMER_DURATION.get() * 20;
        for (AbstractEvent event : ACTIVE_EVENTS.keySet()) {
            if (event instanceof X2TimerEvent) {
                newTotalTicks /= 2;
            } else if (event instanceof X5TimerEvent) {
                newTotalTicks /= 5;
            }
        }
        return totalTicks = newTotalTicks;
    }

    public static void start(MinecraftServer server) {
        TimerBossBar.setBossBarColor(isRunning = true);
        for (Map.Entry<AbstractEvent, Integer> entry : ACTIVE_EVENTS.entrySet()) {
            AbstractEvent event = entry.getKey();
            if (event.getSide() != EventSide.CLIENT){
               if (event instanceof IActiveStateEvent activeStateEvent) activeStateEvent.setActive();
               else server.getAllLevels().forEach(event::onStart);
            }
            if (event.getSide() != EventSide.SERVER) ClientEventPacket.sendClientEventPacket(event, ClientEventPacket.EventType.START, server);
        }
    }

    public static void pause(MinecraftServer server) {
        TimerBossBar.setBossBarColor(isRunning = false);
        for (Map.Entry<AbstractEvent, Integer> entry : ACTIVE_EVENTS.entrySet()) {
            AbstractEvent event = entry.getKey();
            if (event.getSide() != EventSide.CLIENT) server.getAllLevels().forEach(event::onEnd);
            if (event.getSide() != EventSide.SERVER) ClientEventPacket.sendClientEventPacket(event, ClientEventPacket.EventType.END, server);
        }
    }

    public static int getTickCounter() {
        return tickCounter;
    }

    public static void setTickCounter(int tickCounter) {
        ServerTick.tickCounter = tickCounter;
    }

    public static boolean isEnabled() {
        return GeneralConfig.ENABLE_MOD.get();
    }

    public static int getTotalTicks() {
        return totalTicks;
    }

    public static boolean isRun() {
        return isRunning;
    }

    public static Map<AbstractEvent, Integer> getActiveEvents() {
        return ACTIVE_EVENTS;
    }
}