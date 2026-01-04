package com.a3ot.eventhorizon.events.server;

import com.a3ot.eventhorizon.ServerTick;
import com.a3ot.eventhorizon.events.AbstractEvent;
import com.a3ot.eventhorizon.gui.TimerBossBar;
import net.minecraft.server.level.ServerLevel;

public class X5TimerEvent implements AbstractEvent {

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
        return 1;
    }
}
