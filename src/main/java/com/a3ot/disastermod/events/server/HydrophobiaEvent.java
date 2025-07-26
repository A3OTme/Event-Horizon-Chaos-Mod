package com.a3ot.disastermod.events.server;

import com.a3ot.disastermod.data.ModDamageTypes;
import com.a3ot.disastermod.events.AbstractEvent;
import com.a3ot.disastermod.events.subclasses.IActiveStateEvent;
import com.a3ot.disastermod.events.utils.Utils;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;

public class HydrophobiaEvent implements AbstractEvent, IActiveStateEvent {
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
        if (level.dimension() == Level.OVERWORLD) level.setWeatherParameters(0, 99999999, true, false);
        level.players().stream().filter(Utils::isPlayerValid).forEach(player -> {
            BlockPos pos = player.blockPosition().above(3);
            int x = pos.getX();
            int z = pos.getZ();
            int y = pos.getY();
            Utils.blockFilling(level,
                    new BlockPos(x - 1, y, z - 1),
                    new BlockPos(x + 1, y, z + 1),
                    Blocks.SPONGE);
        });
        this.setActive();
    }

    @Override
    public void onEnd(ServerLevel level) {
        level.resetWeatherCycle();
        this.setInactive();
    }

    public static void playerTick(Player player, Level level){
        if(!active) return;
        if(!Utils.isPlayerValid(player)) return;
        if (player.isInWaterRainOrBubble()) {
            DamageSource botanophobiaDamage = level.damageSources().source(ModDamageTypes.HYDROPHOBIA);
            player.hurt(botanophobiaDamage, 2.0F);
        }
    }

    @Override
    public boolean conflictsWith(AbstractEvent other) {
        return other instanceof GillsEvent;
    }
}
