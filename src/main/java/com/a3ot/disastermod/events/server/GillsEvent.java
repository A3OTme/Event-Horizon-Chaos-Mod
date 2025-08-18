package com.a3ot.disastermod.events.server;

import com.a3ot.disastermod.ServerTick;
import com.a3ot.disastermod.events.AbstractEvent;
import com.a3ot.disastermod.events.utils.IActiveStateEvent;
import com.a3ot.disastermod.events.utils.Utils;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.event.entity.living.LivingBreatheEvent;

public class GillsEvent implements AbstractEvent, IActiveStateEvent {
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
        level.players().stream().filter(Utils::isValidPlayer).forEach(player -> {
            MobEffectInstance water_breathing = new MobEffectInstance(MobEffects.WATER_BREATHING, Math.min(Math.max(ServerTick.getTotalTicks() / 2, 200), 1200), 0, false, true);
            player.addEffect(water_breathing);
        });
        this.setActive();
    }

    @Override
    public void onEnd(ServerLevel level) {
        this.setInactive();
    }

    public static void Drowning(LivingBreatheEvent event){
        if(!GillsEvent.active) return;
        LivingEntity entity = event.getEntity();
        if (!(entity instanceof Player player)) return;
        boolean isUnderwater = player.isUnderWater();
        boolean hasWaterBreathing = player.hasEffect(MobEffects.WATER_BREATHING);
        event.setCanBreathe(isUnderwater || hasWaterBreathing);
        if (isUnderwater || hasWaterBreathing) {
            event.setRefillAirAmount(6);
            event.setConsumeAirAmount(-1);
        } else {
            event.setRefillAirAmount(-1);
            event.setConsumeAirAmount(1);
        }
    }

    @Override
    public void playerRespawnOrJoin(Player player, Level level){
        if(!(Utils.isValidPlayer(player))) return;
        MobEffectInstance water_breathing = new MobEffectInstance(MobEffects.WATER_BREATHING, Math.min(Math.max(ServerTick.getTotalTicks() / 2, 200), 1200), 0, false, true);
        player.addEffect(water_breathing);
    }

    @Override
    public boolean conflictsWith(AbstractEvent other) {
        return other instanceof EnderBloodEvent;
    }

    @Override
    public double getDefaultDurationMultiplier() {
        return 2.5;
    }
}
