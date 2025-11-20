package com.a3ot.disastermod.events.subclasses;

import com.a3ot.disastermod.events.AbstractEvent;
import com.a3ot.disastermod.events.utils.Utils;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

import java.util.HashMap;
import java.util.Map;

public abstract class AbstractPlayerAttributeEvent implements AbstractEvent {
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
    public void onStart(ServerLevel level) {
        defineModifiers();
        level.players().stream().filter(Utils::isValidPlayer).forEach(this::applyModifiers);
    }

    @Override
    public void onEnd(ServerLevel level) {
        level.players().forEach(this::restoreOriginalValues);
    }
}
