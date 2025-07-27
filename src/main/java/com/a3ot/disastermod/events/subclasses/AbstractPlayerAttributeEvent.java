package com.a3ot.disastermod.events.subclasses;

import com.a3ot.disastermod.events.utils.Utils;
import net.minecraft.server.level.ServerLevel;

public abstract class AbstractPlayerAttributeEvent extends AbstractAttributeEvent {
    @Override
    public void onStart(ServerLevel level) {
        defineModifiers();
        level.players().stream().filter(Utils::isValidPlayer).forEach(this::applyModifiers);
    }

    @Override
    public void onEnd(ServerLevel level) {
        level.players().stream().filter(Utils::isValidPlayer).forEach(this::restoreOriginalValues);
        modifiers.clear();
    }

    @Override
    public boolean requiresPeriodicClientTick() {
        return false;
    }
}
