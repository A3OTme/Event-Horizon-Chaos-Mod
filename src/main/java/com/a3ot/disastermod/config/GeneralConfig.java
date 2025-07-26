package com.a3ot.disastermod.config;

import java.util.HashMap;
import java.util.Map;

import com.a3ot.disastermod.events.EventsRegistry;
import net.neoforged.neoforge.common.ModConfigSpec;
import com.a3ot.disastermod.events.AbstractEvent;

public class GeneralConfig {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();
    public static final ModConfigSpec SPEC;

    public static final Map<String, EventConfig> EDIT_EVENTS = new HashMap<>();

    public static final ModConfigSpec.BooleanValue ENABLE_MOD = BUILDER.define("enableMod", true);

    public static final ModConfigSpec.IntValue TIMER_DURATION = BUILDER.defineInRange("timerDuration", 60, 5, Integer.MAX_VALUE);

    public record EventConfig(ModConfigSpec.BooleanValue enabled, ModConfigSpec.DoubleValue duration) { }

    static {
        processEvents();
        SPEC = BUILDER.build();
    }

    private static void processEvents() {
        BUILDER.push("editEvents");
        for (AbstractEvent event : EventsRegistry.getDisasterEvents()) {
            String name = event.getConfigName();

            BUILDER.push(name);

            ModConfigSpec.BooleanValue enabled = BUILDER
                    .define("enabled", event.getDefaultEnabled());

            ModConfigSpec.DoubleValue duration = null;
            if (event.getDefaultDurationMultiplier() > 0) {
                duration = BUILDER
                        .defineInRange("durationMultiplier", event.getDefaultDurationMultiplier(), 0.1, 10);
            }
            EDIT_EVENTS.put(name, new EventConfig(enabled, duration));
            BUILDER.pop();
        }
        BUILDER.pop();
    }
}