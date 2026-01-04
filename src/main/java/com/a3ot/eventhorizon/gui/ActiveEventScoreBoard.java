package com.a3ot.eventhorizon.gui;

import com.a3ot.eventhorizon.ServerTick;
import com.a3ot.eventhorizon.events.AbstractEvent;
import com.a3ot.eventhorizon.events.server.UnknownEvent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.scores.*;
import net.minecraft.world.scores.criteria.ObjectiveCriteria;
import net.minecraft.network.chat.Component;
import java.util.*;

public class ActiveEventScoreBoard {
    private static final String OBJECTIVE_NAME = "ActiveEvents";
    private static Objective objective;

    public static void initialize(MinecraftServer server) {
        Scoreboard scoreboard = server.getScoreboard();
        objective = scoreboard.getObjective(OBJECTIVE_NAME);
        if (objective == null) {
            objective = scoreboard.addObjective(
                    OBJECTIVE_NAME,
                    ObjectiveCriteria.DUMMY,
                    Component.translatable("eventhorizon.timer.sidebar.message"),
                    ObjectiveCriteria.RenderType.INTEGER,
                    true,
                    null
            );
            scoreboard.setDisplayObjective(DisplaySlot.SIDEBAR, objective);
        }
    }

    public static void update(MinecraftServer server) {
        if (objective == null) return;

        Scoreboard scoreboard = server.getScoreboard();
        Map<AbstractEvent, Integer> activeEvents = ServerTick.getActiveEvents();

        if (activeEvents.isEmpty() || UnknownEvent.isActive()) {
            if (scoreboard.getDisplayObjective(DisplaySlot.SIDEBAR) == objective) {
                scoreboard.setDisplayObjective(DisplaySlot.SIDEBAR, null);
            }
            return;
        } else {
            if (scoreboard.getDisplayObjective(DisplaySlot.SIDEBAR) != objective) {
                scoreboard.setDisplayObjective(DisplaySlot.SIDEBAR, objective);
            }
        }

        Set<String> currentEventNames = new HashSet<>();
        for (Map.Entry<AbstractEvent, Integer> entry : activeEvents.entrySet()) {
            AbstractEvent event = entry.getKey();
            String eventName = event.getConfigName();
            currentEventNames.add(eventName);
        }

        Collection<ScoreHolder> allHolders = scoreboard.getTrackedPlayers();
        for (ScoreHolder holder : allHolders) {
            String holderName = holder.getScoreboardName();
            if (!currentEventNames.contains(holderName)) {
                scoreboard.resetSinglePlayerScore(holder, objective);
            }
        }

        for (Map.Entry<AbstractEvent, Integer> entry : activeEvents.entrySet()) {
            AbstractEvent event = entry.getKey();
            int remainingSeconds = entry.getValue() / 20 + 1;
            String eventName = event.getConfigName();
            ScoreHolder holder = ScoreHolder.forNameOnly(eventName);
            ScoreAccess score = scoreboard.getOrCreatePlayerScore(holder, objective);
            score.set(remainingSeconds);
            score.display(Component.translatable(event.getName()));
        }
    }

    public static void deinitialize(MinecraftServer server) {
        if (objective != null) {
            server.getScoreboard().removeObjective(objective);
            objective = null;
        }
    }
}