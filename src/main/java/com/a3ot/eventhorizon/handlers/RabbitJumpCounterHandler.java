package com.a3ot.eventhorizon.handlers;

import com.a3ot.eventhorizon.events.utils.Utils;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.animal.Rabbit;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class RabbitJumpCounterHandler {
    private static final Map<UUID, Integer> jumpCounts = new ConcurrentHashMap<>();
    private static final int JUMPS_TO_TRIGGER = 5;

    public static void incrementJumpCount(Rabbit rabbit) {
        UUID rabbitId = rabbit.getUUID();
        int currentCount = jumpCounts.getOrDefault(rabbitId, 0);
        currentCount++;
        jumpCounts.put(rabbitId, currentCount);
        if (currentCount >= JUMPS_TO_TRIGGER) {
            triggerRabbitEvent(rabbit);
            jumpCounts.put(rabbitId, 0);
        }
    }

    private static void triggerRabbitEvent(Rabbit rabbit) {
        Utils.ChorusTeleport(rabbit, rabbit.level(), 5, 10);
        MobEffectInstance glowing = new MobEffectInstance(MobEffects.GLOWING, 60, 0, false, false);
        rabbit.addEffect(glowing);
    }

    public static int getJumpCount(Rabbit rabbit) {
        return jumpCounts.getOrDefault(rabbit.getUUID(), 0);
    }

    public static void clearCounters() {
        jumpCounts.clear();
    }
}