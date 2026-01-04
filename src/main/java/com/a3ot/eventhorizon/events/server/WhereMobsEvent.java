package com.a3ot.eventhorizon.events.server;

import com.a3ot.eventhorizon.events.AbstractEvent;
import com.a3ot.eventhorizon.events.utils.Utils;
import net.minecraft.ChatFormatting;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import java.util.List;

public class WhereMobsEvent implements AbstractEvent {
    @Override
    public void onTick(ServerLevel level) {
        level.getAllEntities().forEach(entity -> {
            if (entity instanceof Mob livingEntity) {
                livingEntity.setSilent(true);
                livingEntity.setInvisible(true);
            }
        });
    }

    @Override
    public void onEnd(ServerLevel level) {
        level.getAllEntities().forEach(entity -> {
            if (entity instanceof Mob livingEntity) {
                livingEntity.setSilent(false);
                livingEntity.setInvisible(false);
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
        List<LivingEntity> nearbyMobs = player.level().getEntitiesOfClass(
                LivingEntity.class,
                player.getBoundingBox().inflate(10)
        );
        for (LivingEntity mob : nearbyMobs) {
            if (mob instanceof Player) continue;
            MobEffectInstance glowing = new MobEffectInstance(MobEffects.GLOWING, 100, 0, false, false);
            mob.addEffect(glowing);
        }
    }

    @Override
    public ChatFormatting getColor() {
        return ChatFormatting.WHITE;
    }
}
