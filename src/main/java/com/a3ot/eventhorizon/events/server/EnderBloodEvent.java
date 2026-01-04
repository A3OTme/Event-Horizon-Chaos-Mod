package com.a3ot.eventhorizon.events.server;

import com.a3ot.eventhorizon.data.ModDamageTypes;
import com.a3ot.eventhorizon.events.AbstractEvent;
import com.a3ot.eventhorizon.events.EventType;
import com.a3ot.eventhorizon.events.both.FlyingFishEvent;
import com.a3ot.eventhorizon.events.utils.IActiveStateEvent;
import com.a3ot.eventhorizon.events.utils.Utils;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class EnderBloodEvent implements AbstractEvent, IActiveStateEvent {
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
        if (level.isRaining()){
            level.players().stream().filter(Utils::isValidPlayer).forEach(player -> {
                BlockPos pos = player.blockPosition().above(3);
                int x = pos.getX();
                int z = pos.getZ();
                int y = pos.getY();
                Utils.blockFilling(level,
                        new BlockPos(x - 1, y, z - 1),
                        new BlockPos(x + 1, y, z + 1),
                        Blocks.SPONGE);
            });
        }
        this.setActive();
    }

    @Override
    public void onEnd(ServerLevel level) {
        this.setInactive();
        Utils.clearTeleportCooldowns();
    }

    public static void playerTick(Player player, Level level){
        if(!active) return;
        if (!(player instanceof ServerPlayer serverPlayer)) return;
        if(!Utils.isValidPlayer(player)) return;
        if (player.isInWaterRainOrBubble()) {
            DamageSource hydrophobiaDamage = level.damageSources().source(ModDamageTypes.HYDROPHOBIA);
            player.hurt(hydrophobiaDamage, 2.0F);
        }

        if (Utils.isOnCooldown(player)) return;

        ItemStack headItem = player.getItemBySlot(EquipmentSlot.HEAD);
        if (headItem.is(Blocks.CARVED_PUMPKIN.asItem())) return;

        List<LivingEntity> nearbyMobs = player.level().getEntitiesOfClass(
                LivingEntity.class,
                player.getBoundingBox().inflate(10)
        );
        for (LivingEntity mob : nearbyMobs) {
            if (mob == player) continue;
            if (isLookingAtMob(serverPlayer, mob)) {
                scream(serverPlayer);
                Utils.ChorusTeleport(serverPlayer, serverPlayer.serverLevel());
                player.level().playSound(null, player.blockPosition(), SoundEvents.ENDERMAN_SCREAM, SoundSource.PLAYERS);
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

    public static void scream(Player player){
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
    }

    public static void livingDamage(LivingEntity entity) {
        if (!active) return;
        if (!(entity instanceof ServerPlayer player)) return;
        if (Utils.isOnCooldown(player)) return;
        Utils.ChorusTeleport(player, player.serverLevel());
    }

    @Override
    public EventType getType() {
        return EventType.NEUTRAL;
    }

    @Override
    public ChatFormatting getColor() {
        return ChatFormatting.DARK_PURPLE;
    }

    @Override
    public boolean conflictsWith(AbstractEvent other) {
        return other instanceof FlyingFishEvent;
    }
}
