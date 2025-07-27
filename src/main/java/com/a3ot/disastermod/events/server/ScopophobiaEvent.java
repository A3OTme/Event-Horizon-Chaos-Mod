package com.a3ot.disastermod.events.server;

import com.a3ot.disastermod.events.AbstractEvent;
import com.a3ot.disastermod.events.subclasses.IActiveStateEvent;
import com.a3ot.disastermod.events.utils.Utils;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ScopophobiaEvent implements AbstractEvent, IActiveStateEvent {
    private static boolean active = false;
    private static final Map<Player, Long> cooldowns = new HashMap<>();
    private static final long COOLDOWN_DURATION = 60;

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

    public static void playerTick(Player player) {
        if (!active) return;
        if (!(player instanceof ServerPlayer serverPlayer)) return;
        if (!Utils.isValidPlayer(serverPlayer)) return;
        long currentTime = player.level().getGameTime();
        if (cooldowns.containsKey(player) && cooldowns.get(player) > currentTime) return;
        ItemStack headItem = player.getItemBySlot(EquipmentSlot.HEAD);
        if (headItem.is(Blocks.CARVED_PUMPKIN.asItem())) return;

        List<LivingEntity> nearbyMobs = player.level().getEntitiesOfClass(
                LivingEntity.class,
                player.getBoundingBox().inflate(10)
        );
        for (LivingEntity mob : nearbyMobs) {
            if (mob == player) continue;
            if (isLookingAtMob(serverPlayer, mob)) {
                fright(player, mob);
                cooldowns.put(player, currentTime + COOLDOWN_DURATION);
                break;
            }
        }
    }

    private static boolean isLookingAtMob(ServerPlayer player, LivingEntity mob) {
        Vec3 playerEye = player.getEyePosition();
        Vec3 mobEye = mob.getEyePosition();

        Vec3 directionToMob = mobEye.subtract(playerEye).normalize();
        Vec3 playerLook = player.getLookAngle().normalize();

        double distance = player.distanceTo(mob);
        double threshold = 1.0 - (0.012 / distance);
        double playerDot = directionToMob.dot(playerLook);

        if (playerDot <= threshold) return false;
        if (!player.hasLineOfSight(mob)) return false;
        if (player.distanceToSqr(mob) > 100.0) return false;

        Vec3 directionToPlayer = playerEye.subtract(mobEye).normalize();
        Vec3 mobLook = mob.getLookAngle().normalize();
        double mobDot = directionToPlayer.dot(mobLook);
        return !(mobDot <= Math.cos(Math.toRadians(30)));
    }

    public static void fright(Player player, LivingEntity mob){
        if (!player.isAlive() || player.hurtMarked || mob.isDeadOrDying() || player.hasEffect(MobEffects.WIND_CHARGED)) return;
        player.level().playSound(null, player.blockPosition(), SoundEvents.BAT_HURT, SoundSource.PLAYERS, 1F, 0.5F);
        Vec3 directionToPlayer = player.getEyePosition().subtract(mob.getEyePosition()).normalize();
        double pushStrength = 1.0;
        double verticalLift = 0.5;
        player.setDeltaMovement(
                directionToPlayer.x * pushStrength,
                verticalLift,
                directionToPlayer.z * pushStrength
        );
        player.hurtMarked = true;
        ItemStack mainHand = player.getItemInHand(InteractionHand.MAIN_HAND);
        ItemStack offHand = player.getItemInHand(InteractionHand.OFF_HAND);
        if (!mainHand.isEmpty()) {
            player.drop(mainHand, true);
            player.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
        }
        if (!offHand.isEmpty()) {
            player.drop(offHand, true);
            player.setItemInHand(InteractionHand.OFF_HAND, ItemStack.EMPTY);
        }
        MobEffectInstance wind_charged = new MobEffectInstance(MobEffects.WIND_CHARGED, 60, 0, false, false);
        player.addEffect(wind_charged);
    }

    @Override
    public int getInterval() {
        return 5;
    }
}