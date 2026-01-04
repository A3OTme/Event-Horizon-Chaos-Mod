package com.a3ot.eventhorizon.events.server;

import com.a3ot.eventhorizon.events.AbstractEvent;
import com.a3ot.eventhorizon.events.utils.IActiveStateEvent;
import net.minecraft.ChatFormatting;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class DamageBringsToExplosionEvent implements AbstractEvent, IActiveStateEvent {
    private static boolean active = false;

    @Override
    public void setActive() {
        active = true;
    }

    @Override
    public void setInactive() {
        active = false;
    }

    public static boolean isActive() {
        return active;
    }

    @Override
    public void onStart(ServerLevel level) {
        this.setActive();
    }

    @Override
    public void onEnd(ServerLevel level) {
        this.setInactive();
    }

    public static void livingDamage(LivingEntity entity, @Nullable DamageSource damageSource) {
        if (!active || damageSource == null) return;
        if (damageSource.is(DamageTypes.EXPLOSION)) return;
        entity.level().explode(entity, entity.getX(), entity.getY(), entity.getZ(), 4, Level.ExplosionInteraction.MOB);
    }

    @Override
    public ChatFormatting getColor() {
        return ChatFormatting.RED;
    }
}
