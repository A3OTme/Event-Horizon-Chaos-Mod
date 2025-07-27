package com.a3ot.disastermod.events.server;

import com.a3ot.disastermod.events.AbstractEvent;
import com.a3ot.disastermod.events.utils.Utils;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class PhotosensitizationEvent implements AbstractEvent {
    @Override
    public void onStart(ServerLevel level){
        level.players().stream().filter(Utils::isValidPlayer).forEach(player -> {
            MobEffectInstance fire_resistance = new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 200, 0, false, true);
            player.addEffect(fire_resistance);
        });
    }

    @Override
    public void onTick(ServerLevel level) {
        level.players().stream().filter(Utils::isValidPlayer).forEach(player -> {
            BlockPos playerPos = player.blockPosition();
            int lightLevel = Utils.lightManager(playerPos, level, 0, 0);
            if (lightLevel > 12) {
                boolean isProtected = player.isInWaterRainOrBubble() || player.isInPowderSnow || player.wasInPowderSnow;
                if (!isProtected) {
                    //Due to the logic of fire damage, have to call the method inside onTick, not in PlayerTickEvent.
                    player.igniteForSeconds(3.0F);
                }
            }
        });
    }

    @Override
    public boolean requiresPeriodicTick() {
        return true;
    }

    @Override
    public void playerRespawnOrJoin(Player player, Level level){
        if(!(Utils.isValidPlayer(player))) return;
        MobEffectInstance fire_resistance = new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 200, 0, false, true);
        player.addEffect(fire_resistance);
    }


    @Override
    public boolean conflictsWith(AbstractEvent other) {
        return other instanceof NyctophobiaEvent;
    }
}
