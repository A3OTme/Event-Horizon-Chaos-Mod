package com.a3ot.disastermod.events.server;

import com.a3ot.disastermod.ServerTick;
import com.a3ot.disastermod.events.AbstractEvent;
import com.a3ot.disastermod.gui.TimerBossBar;
import net.minecraft.server.level.ServerLevel;

public class X2TimerEvent implements AbstractEvent {

    @Override
    public void onStart(ServerLevel level){
        ServerTick.updateTotalTicks();
        TimerBossBar.update(ServerTick.getTickCounter(), ServerTick.getTotalTicks());
    }

    @Override
    public void onEnd(ServerLevel level){
        ServerTick.updateTotalTicks();
    }

    @Override
    public double getDefaultDurationMultiplier() {
        return 2.5;
    }
}
