package com.a3ot.eventhorizon.events.subclasses;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;

public abstract class AbstractAttributeEvent extends AbstractPlayerAttributeEvent {

    @Override
    public void onTick(ServerLevel level) {
        defineModifiers();
        level.getAllEntities().forEach(entity -> {
            if (entity instanceof LivingEntity living) {
                applyModifiers(living);
            }
        });
    }

    @Override
    public void onEnd(ServerLevel level) {
        level.getAllEntities().forEach(entity -> {
            if (entity instanceof LivingEntity living) {
                restoreOriginalValues(living);
            }
        });
    }

    @Override
    public boolean requiresPeriodicTick() {
        return true;
    }
}
