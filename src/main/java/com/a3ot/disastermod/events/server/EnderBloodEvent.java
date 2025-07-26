package com.a3ot.disastermod.events.server;

import com.a3ot.disastermod.events.AbstractEvent;
import com.a3ot.disastermod.events.EventType;
import com.a3ot.disastermod.events.subclasses.IActiveStateEvent;
import net.minecraft.ChatFormatting;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;

import java.util.HashMap;
import java.util.Map;

public class EnderBloodEvent implements AbstractEvent, IActiveStateEvent {
    private static boolean active = false;
    private static final Map<Player, Long> cooldowns = new HashMap<>();
    private static final long COOLDOWN_DURATION = 100;

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
        cooldowns.clear();
    }

    public static void livingDamage(LivingEntity entity) {
        if (!active) return;
        if (!(entity instanceof ServerPlayer player)) return;
        long currentTime = player.level().getGameTime();
        if (cooldowns.containsKey(player) && cooldowns.get(player) > currentTime) return;

        ServerLevel level = (ServerLevel) player.level();
        Vec3 originalPos = player.position();
        for (int i = 0; i < 16; i++) {
            double d0 = player.getX() + (player.getRandom().nextDouble() - 0.5) * 128.0;
            double d1 = Mth.clamp(
                    player.getY() + (double)(player.getRandom().nextInt(48) - 8),
                    level.getMinBuildHeight(),
                    level.getMinBuildHeight() + level.getLogicalHeight() - 1
            );
            double d2 = player.getZ() + (player.getRandom().nextDouble() - 0.5) * 128.0;
            double dx = d0 - originalPos.x();
            double dz = d2 - originalPos.z();
            double distance = Math.sqrt(dx * dx + dz * dz);
            if (distance < 16 || distance > 48) continue;
            if (player.isPassenger()) player.stopRiding();
            Vec3 vec3 = player.position();
            net.neoforged.neoforge.event.entity.EntityTeleportEvent.ChorusFruit event =
                    net.neoforged.neoforge.event.EventHooks.onChorusFruitTeleport(player, d0, d1, d2);
            if (player.randomTeleport(event.getTargetX(), event.getTargetY(), event.getTargetZ(), true)) {
                cooldowns.put(player, currentTime + COOLDOWN_DURATION);
                level.gameEvent(GameEvent.TELEPORT, vec3, GameEvent.Context.of(player));
                SoundSource soundsource = SoundSource.PLAYERS;
                level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.CHORUS_FRUIT_TELEPORT, soundsource);
                player.resetFallDistance();
                break;
            }
        }
    }

    @Override
    public EventType getType() {
        return EventType.NEUTRAL;
    }

    @Override
    public ChatFormatting getColor() {
        return ChatFormatting.DARK_PURPLE;
    }
}
