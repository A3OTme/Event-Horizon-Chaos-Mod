package com.a3ot.eventhorizon.config;

import net.neoforged.neoforge.common.ModConfigSpec;

public class ClientConfig {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();
    public static final ModConfigSpec SPEC;

    public static ModConfigSpec.BooleanValue CLIENT_EVENT_SOUND = BUILDER.define("eventSound", true);

    public static ModConfigSpec.BooleanValue CLIENT_WARNING_SOUND = BUILDER.define("countdownSound", true);

    static {
        SPEC = BUILDER.build();
    }
}
