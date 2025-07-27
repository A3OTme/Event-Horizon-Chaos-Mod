package com.a3ot.disastermod.events.subclasses;

import com.a3ot.disastermod.events.AbstractEvent;
import com.a3ot.disastermod.events.utils.Utils;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

import java.util.HashMap;
import java.util.Map;

public abstract class AbstractAttributeEvent implements AbstractEvent {
    protected final Map<Holder<Attribute>, AttributeModifier> modifiers = new HashMap<>();

    protected void applyModifiers(LivingEntity entity) {
        modifiers.forEach((attribute, modifier) -> {
            AttributeInstance instance = entity.getAttribute(attribute);
            if (instance != null && !instance.hasModifier(modifier.id())) {
                instance.addTransientModifier(modifier);
            }
        });
    }

    protected void restoreOriginalValues(LivingEntity entity) {
        modifiers.forEach((attribute, modifier) -> {
            AttributeInstance instance = entity.getAttribute(attribute);
            if (instance != null) {
                instance.removeModifier(modifier.id());
            }
        });
    }

    protected abstract void defineModifiers();

    @Override
    public void onTick(ServerLevel level) {
        defineModifiers();
        level.getAllEntities().forEach(entity -> {
            if (entity instanceof LivingEntity living &&
                    !(entity instanceof ServerPlayer player && !Utils.isValidPlayer(player))) {
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
        modifiers.clear();
    }

    @Override
    public boolean requiresPeriodicTick() {
        return true;
    }
}
