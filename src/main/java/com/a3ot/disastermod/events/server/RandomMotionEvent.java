package com.a3ot.disastermod.events.server;

import com.a3ot.disastermod.events.AbstractEvent;
import com.a3ot.disastermod.events.utils.Utils;
import net.minecraft.server.level.ServerLevel;

public class RandomMotionEvent implements AbstractEvent {
    @Override
    public void onTick(ServerLevel level) {
        level.players().stream().filter(Utils::isValidPlayer).forEach(player -> {
            double angle = level.random.nextDouble() * 2 * Math.PI;
            double power = 1;
            double x = Math.cos(angle) * power;
            double z = Math.sin(angle) * power;
            double y = 0.3;
            player.setDeltaMovement(x, y, z);
            player.hurtMarked = true;
        });
    }

    @Override
    public boolean requiresPeriodicTick() {
        return true;
    }

    @Override
    public int getInterval() {
        return 60;
    }
}
