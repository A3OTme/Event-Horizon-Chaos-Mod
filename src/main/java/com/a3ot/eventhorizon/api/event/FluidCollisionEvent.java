package com.a3ot.eventhorizon.api.event;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.material.FluidState;
import net.neoforged.bus.api.ICancellableEvent;
import net.neoforged.neoforge.event.entity.living.LivingEvent;

public class FluidCollisionEvent extends LivingEvent implements ICancellableEvent {
    private final FluidState fluid;

    public FluidCollisionEvent(LivingEntity entity, FluidState fluid) {
        super(entity);
        this.fluid = fluid;
    }

    public FluidState getFluid() {
        return fluid;
    }
}
